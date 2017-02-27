package cz.kinst.jakub.sample.firestorm;


import com.google.firebase.database.DataSnapshot;

import cz.kinst.jakub.firestorm.FirebaseItemViewModel;


public class ToDoItemViewModel extends FirebaseItemViewModel<ToDoItem> {
	private final ToDoFirebaseWriter toDoFirebaseWriter;


	protected ToDoItemViewModel(DataSnapshot dataSnapshot, ToDoFirebaseWriter toDoFirebaseWriter) {
		super(dataSnapshot);
		this.toDoFirebaseWriter = toDoFirebaseWriter;
	}


	@Override
	public ToDoItem parseDataSnapshot(DataSnapshot dataSnapshot) {
		ToDoItem todo = dataSnapshot.getValue(ToDoItem.class);
		todo.setId(dataSnapshot.getKey());
		return todo;
	}


	public void onCheckedChanged(boolean checked) {
		if(getItem().get() != null)
			toDoFirebaseWriter.setChecked(getItem().get().getId(), checked);
	}


	public void remove() {
		toDoFirebaseWriter.remove(getItem().get().getId());
	}
}
