package uzuzjmd.competence.service.rest.client.dto;

import java.util.Comparator;

public class TreeEntryComparator implements Comparator<AbstractTreeEntry> {

	@Override
	public int compare(AbstractTreeEntry arg0, AbstractTreeEntry arg1) {
		return compare(arg0.getName(), arg1.getName());
	}

	private int compare(String str1, String str2) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
		if (res == 0) {
			res = str1.compareTo(str2);
		}
		return res;
	}
}
