package cz.kinst.jakub.sample.firestorm;


public class ToDoItem {
	boolean checked;
	String title;
	private String id;


	public ToDoItem() {
	}


	public ToDoItem(String title) {
		this.title = title;
	}


	public boolean isChecked() {
		return checked;
	}


	public String getTitle() {
		return title;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
}
