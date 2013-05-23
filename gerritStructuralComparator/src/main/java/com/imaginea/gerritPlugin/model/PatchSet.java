package com.imaginea.gerritPlugin.model;

import java.util.HashMap;
import java.util.Map;

public class PatchSet {
	private Map<String, Patch> patchMapper = new HashMap<String, Patch>();
	private int patchSetId;
	private int changeId;

	public Map<String, Patch> getPatchMapper() {
		return new HashMap<String, Patch>(patchMapper);
	}

	public Patch getPatch(String patchFileName) {
		return patchMapper.get(patchFileName);
	}

	public void addPatch2Mapper(String patchFileName, Patch patch) {
		patchMapper.put(patchFileName, patch);
	}

	public void addPatchs2Mapper(Map<String, Patch> patchMapper) {
		this.patchMapper.putAll(patchMapper);
	}

	public int getPatchSetId() {
		return patchSetId;
	}

	public void setPatchSetId(int patchSetId) {
		this.patchSetId = patchSetId;
	}

	public int getChangeId() {
		return changeId;
	}

	public void setChangeId(int changeId) {
		this.changeId = changeId;
	}

}
