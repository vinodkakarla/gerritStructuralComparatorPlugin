package com.imaginea.gerritPlugin.model;

public class Patch {
	private String patchFileName;
	private String patchParentKey;
	private String changeType;
	private String patchType;
	private String fileData;

	public String getPatchFileName() {
		return patchFileName;
	}

	public void setPatchFileName(String patchFileName) {
		this.patchFileName = patchFileName;
	}

	public String getPatchParentKey() {
		return patchParentKey;
	}

	public void setPatchParentKey(String patchParentKey) {
		this.patchParentKey = patchParentKey;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public String getPatchType() {
		return patchType;
	}

	public void setPatchType(String patchType) {
		this.patchType = patchType;
	}

	public String getFileData() {
		return fileData;
	}

	public void setFileData(String fileData) {
		this.fileData = fileData;
	}
}
