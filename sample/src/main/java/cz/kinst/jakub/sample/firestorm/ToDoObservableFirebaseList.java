package cz.kinst.jakub.sample.firestorm;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import cz.kinst.jakub.firestorm.ObservableFirebaseList;


public class ToDoObservableFirebaseList extends ObservableFirebaseList<ToDoItemViewModel> {
	private final ToDoFirebaseWriter toDoFirebaseWriter;


	public ToDoObservableFirebaseList(ToDoFirebaseWriter toDoFirebaseWriter) {
		super(FirebaseDatabase.getInstance().getReference("todos"));
		this.toDoFirebaseWriter = toDoFirebaseWriter;
	}


	@Override
	public ToDoItemViewModel createListItem(DataSnapshot dataSnapshot) {
		return new ToDoItemViewModel(dataSnapshot, toDoFirebaseWriter);
	}


	@Override
	protected void onDatabaseError(DatabaseError error) {
		error.toException().printStackTrace();
	}
}
