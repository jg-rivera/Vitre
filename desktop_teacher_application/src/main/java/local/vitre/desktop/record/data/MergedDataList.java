package local.vitre.desktop.record.data;

import java.util.AbstractList;
import java.util.List;

public class MergedDataList<E> extends AbstractList<E> {

	private final List<E> list1;
	private final List<E> list2;
	private String name;
	private int id;

	public MergedDataList(List<E> list1, List<E> list2) {
		this.list1 = list1;
		this.list2 = list2;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public E get(int index) {
		if (index < list1.size()) {
			return list1.get(index);
		}
		return list2.get(index - list1.size());
	}

	@Override
	public int size() {
		return list1.size() + list2.size();
	}
}