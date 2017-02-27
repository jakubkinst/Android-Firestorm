package cz.kinst.jakub.firestorm;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;


public abstract class ObservableFirebaseList<T extends FirebaseItemViewModel> extends InitializableObservableArrayList<T> implements FirebaseArrayModel.OnChangedListener {

	private FirebaseArrayModel firebaseArrayModel;


	/**
	 * @param query Firebase Realtime Database Query determining data collection that should be watched
	 */
	public ObservableFirebaseList(Query query) {
		firebaseArrayModel = new FirebaseArrayModel(query) {
			@Override
			protected void onDatabaseError(DatabaseError error) {
				ObservableFirebaseList.this.onDatabaseError(error);
			}
		};
		firebaseArrayModel.setOnChangedListener(this);
	}


	/**
	 * Implement this method to build your desired ItemViewModel based on the {@link DataSnapshot} from Firebase
	 */
	public abstract T createListItem(DataSnapshot dataSnapshot);

	/**
	 * Called upon any error returned by Firebase
	 *
	 * @param error
	 */
	protected abstract void onDatabaseError(DatabaseError error);


	@Override
	public void onChanged(EventType type, int index, int oldIndex) {
		switch(type) {
			case Initialized:
				ArrayList<T> items = new ArrayList<T>();
				for(DataSnapshot dataSnapshot : firebaseArrayModel.getSnapshots()) {
					items.add(createListItem(dataSnapshot));
				}
				initialize(items);
				break;
			case Added:
				add(index, createListItem(firebaseArrayModel.getSnapshot(index)));
				break;
			case Changed:
				set(index, createListItem(firebaseArrayModel.getSnapshot(index)));
				break;
			case Moved:
				T item = remove(oldIndex);
				add(index, item);
				break;
			case Removed:
				remove(index);
		}
	}


	/**
	 * Starts watching firebase reference - links the {@link com.google.firebase.database.ChildEventListener}
	 * <p>
	 * Usually this should be called in onResume() method within Activity/Fragment/ViewModel
	 */
	public void startWatching() {
		firebaseArrayModel.startWatching();
	}


	/**
	 * Stops watching firebase reference - unlinks the {@link com.google.firebase.database.ChildEventListener}
	 * <p>
	 * Usually this should be called in onPause() method within Activity/Fragment/ViewModel
	 */
	public void stopWatching() {
		firebaseArrayModel.stopWatching();
	}
}
