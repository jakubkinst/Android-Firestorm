package cz.kinst.jakub.sample.firestorm;


import com.google.firebase.database.FirebaseDatabase;


public class ToDoFirebaseWriter {


	public static final String PATH_TODOS = "todos";


	public void add(ToDoItem toDoItem) {
		FirebaseDatabase.getInstance().getReference(PATH_TODOS).push().setValue(toDoItem);
	}


	public void remove(String toDoItemId) {
		FirebaseDatabase.getInstance().getReference(PATH_TODOS).child(toDoItemId).removeValue();
	}


	public void setChecked(String toDoItemId, boolean checked) {
		FirebaseDatabase.getInstance().getReference(PATH_TODOS).child(toDoItemId).child("checked").setValue(checked);
	}
}
