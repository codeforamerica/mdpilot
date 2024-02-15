#!/bin/bash
#
# Precursor:  brew install yq
#
# Run this in the project root directory, or adjust the paths below
# To run this on
# /scripts/create_templates.sh mdBenefitsFlow 0

flowName="${1:-mdBenefitsFlow}"
# index of the flow in the file, 0 based (generally you just want 0, anyways)
flowIndex="${2:0}"
templates=`ls -1 src/main/resources/templates/mdBenefitsFlow/*.html | cut -d'/' -f6-`
screens=( $(yq '[select(document_index == '"$flowIndex"') | .flow.*.nextScreens[0].name] | flatten' src/main/resources/flows-config.yaml) )
screenNames=( $( printf '%s\n' ${screens[@]} | egrep -v '^(---|-|null|\[*\])' ) )
for i in "${!screenNames[@]}"; do
  if grep -q "${screenNames[$i]}.html" <<< "${templates[*]}"; then
   echo "found ${screenNames[$i]} in templates"
  else
    echo "${screenNames[$i]} not in templates; creating one"
    cat scripts/scaffold.html >> "src/main/resources/templates/${flowName}/${screenNames[$i]}.html"
  fi
done
