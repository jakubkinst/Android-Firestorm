package cz.kinst.jakub.sample.firestorm;


import me.tatarka.bindingcollectionadapter.ItemView;


public class MainViewModel {

	public ItemView itemView = ItemView.of(BR.viewModel, R.layout.item_todo);
	private ToDoFirebaseWriter toDoFirebaseWriter = new ToDoFirebaseWriter();
	public ToDoObservableFirebaseList items = new ToDoObservableFirebaseList(toDoFirebaseWriter);


	void addToDo(ToDoItem toDoItem) {
		toDoFirebaseWriter.add(toDoItem);
	}


	void onResume() {
		items.startWatching();
	}


	void onPause() {
		items.stopWatching();
	}
}
