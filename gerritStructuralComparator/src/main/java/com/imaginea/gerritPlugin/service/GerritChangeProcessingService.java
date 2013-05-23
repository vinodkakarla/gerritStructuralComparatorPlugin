package com.imaginea.gerritPlugin.service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gerrit.reviewdb.client.Patch;
import com.google.gerrit.reviewdb.server.ReviewDb;
import com.google.gerrit.server.patch.PatchList;
import com.google.gerrit.server.patch.PatchListCache;
import com.google.gerrit.server.patch.PatchListNotAvailableException;
import com.google.gwtorm.server.OrmException;
import com.google.gwtorm.server.ResultSet;
import com.google.gwtorm.server.SchemaFactory;
import com.imaginea.gerritPlugin.model.Change;
import com.imaginea.gerritPlugin.model.PatchSet;
import com.imaginea.gerritPlugin.utils.FileDataRetrivalService;

public class GerritChangeProcessingService {

	private String requestHostName = null;

	public List<Change> fetchChanges(final SchemaFactory<ReviewDb> dbFactory,
			final PatchListCache patchListCache, String reqHostName)
			throws MalformedURLException, OrmException,
			PatchListNotAvailableException {
		ReviewDb reviewDb = dbFactory.open();

		requestHostName = reqHostName;

		List<Change> changesList = new ArrayList<Change>();

		for (com.google.gerrit.reviewdb.client.Change change : reviewDb
				.changes().all().toList()) {

			Change custChange = buildCustomChange(change);
			custChange.setOwner(reviewDb.accounts().get(change.getOwner())
					.getFullName());

			ResultSet<com.google.gerrit.reviewdb.client.PatchSet> rs = reviewDb
					.patchSets().byChange(change.getId());
			List<com.google.gerrit.reviewdb.client.PatchSet> patchSetsByChangeID = rs
					.toList();

			List<PatchSet> customPatchSets = buildPatchSets(
					patchSetsByChangeID, patchListCache, change);

			custChange.setPatchSets(customPatchSets);
			changesList.add(custChange);

		}

		return changesList;

	}

	private List<PatchSet> buildPatchSets(
			List<com.google.gerrit.reviewdb.client.PatchSet> patchSetsByChangeID,
			PatchListCache patchListCache,
			com.google.gerrit.reviewdb.client.Change change)
			throws PatchListNotAvailableException, MalformedURLException {

		List<PatchSet> customPatchSets = new ArrayList<PatchSet>();

		for (com.google.gerrit.reviewdb.client.PatchSet patchSet : patchSetsByChangeID) {
			PatchSet customPatchSet = new PatchSet();
			customPatchSet.setPatchSetId(patchSet.getPatchSetId());
			customPatchSet.setChangeId(change.getChangeId());
			PatchList list = patchListCache.get(change, patchSet);

			final List<Patch> patches = list.toPatchList(patchSet.getId());
			
			customPatchSet.addPatchs2Mapper(buildPatches(patches));
			
			
			customPatchSets.add(customPatchSet);
		}

		return customPatchSets;
	}

	private Map<String, com.imaginea.gerritPlugin.model.Patch> buildPatches(List<Patch> patches) throws MalformedURLException {
		 Map<String, com.imaginea.gerritPlugin.model.Patch> patchMapper = new HashMap<String, com.imaginea.gerritPlugin.model.Patch>();
		 
		for (Patch patch : patches) {
			if ("/COMMIT_MSG".equals(patch.getFileName()))
				continue;
			com.imaginea.gerritPlugin.model.Patch customPatch = new com.imaginea.gerritPlugin.model.Patch();
			customPatch.setPatchFileName(patch.getFileName());
			customPatch.setPatchParentKey(patch.getKey().getParentKey().toString());
			customPatch.setChangeType(patch.getChangeType().name());
			customPatch.setPatchType(patch.getPatchType().name());
			
			String fileURL = "http://" + requestHostName + "/cat/"
					+ customPatch.getPatchParentKey() + ","
					+ customPatch.getPatchFileName() + "^0";

			customPatch.setFileData(FileDataRetrivalService.getFileDataStream(fileURL));

			patchMapper.put(customPatch.getPatchFileName(),	customPatch);
		}
		return patchMapper;
	}

	private Change buildCustomChange(
			com.google.gerrit.reviewdb.client.Change change) {
		Change custChange = new Change();
		custChange.setBranch(change.getDest().getShortName());
		custChange.setChangeID(change.getChangeId());
		custChange.setChangeKey(change.getKey().get());
		custChange.setCreationDate(change.getCreatedOn());
		custChange.setLastUpdationDate(change.getLastUpdatedOn());
		custChange.setProjectName(change.getProject().get());
		custChange.setSubject(change.getSubject());
		custChange.setChangeStatus(change.getStatus().name());
		return custChange;
	}

}
