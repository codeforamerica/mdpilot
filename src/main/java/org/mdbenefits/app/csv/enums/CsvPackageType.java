package org.mdbenefits.app.csv.enums;

import java.util.List;

public enum CsvPackageType {

  // TODO: abstract environment
  ECE_PACKAGE (List.of(CsvType.PARENT_GUARDIAN, CsvType.STUDENT, CsvType.RELATIONSHIP, CsvType.ECE_APPLICATION), "/la-du-moveit-transfer/nola-ps"),
  WIC_PACKAGE (List.of(CsvType.WIC_APPLICATION), "/la-du-moveit-transfer/dcfs");

  private final List<CsvType> csvTypeList;

  private final String uploadLocation;
  CsvPackageType(List<CsvType> csvTypeList, String uploadLocation) {
    this.csvTypeList = csvTypeList;
    this.uploadLocation = uploadLocation;
  }

  public List<CsvType> getCsvTypeList() {
    return this.csvTypeList;
  }

  public String getUploadLocation() {
    return this.uploadLocation;
  }
}
