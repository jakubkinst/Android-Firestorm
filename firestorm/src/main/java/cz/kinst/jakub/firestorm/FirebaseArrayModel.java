package cz.kinst.jakub.firestorm;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


abstract class FirebaseArrayModel implements ChildEventListener {
	private Query mQuery;
	private OnChangedListener mListener;
	private List<DataSnapshot> mSnapshots = new ArrayList<>();
	private boolean mInitialized = false;


	interface OnChangedListener {
		enum EventType {Added, Changed, Removed, Initialized, Moved}
		void onChanged(EventType type, int index, int oldIndex);
	}


	FirebaseArrayModel(Query ref) {
		mQuery = ref;
	}


	protected abstract void onDatabaseError(DatabaseError firebaseError);


	// Start of ChildEventListener methods
	public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

		for(int i = 0; i < mSnapshots.size(); i++) {
			DataSnapshot dS = mSnapshots.get(i);
			if(dS.getKey().equals(snapshot.getKey())) {// already contains this snapshot
				return;
			}
		}


		int index = 0;
		if(previousChildKey != null) {
			index = getIndexForKey(previousChildKey) + 1;
		}
		mSnapshots.add(index, snapshot);
		notifyChangedListeners(OnChangedListener.EventType.Added, index);
	}


	public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
		int index = getIndexForKey(snapshot.getKey());
		mSnapshots.set(index, snapshot);
		notifyChangedListeners(OnChangedListener.EventType.Changed, index);
	}


	public void onChildRemoved(DataSnapshot snapshot) {
		int index = getIndexForKey(snapshot.getKey());
		mSnapshots.remove(index);
		notifyChangedListeners(OnChangedListener.EventType.Removed, index);
	}


	public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
		int oldIndex = getIndexForKey(snapshot.getKey());
		mSnapshots.remove(oldIndex);
		int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
		mSnapshots.add(newIndex, snapshot);
		notifyChangedListeners(OnChangedListener.EventType.Moved, newIndex, oldIndex);
	}


	public void onCancelled(DatabaseError error) {
		onDatabaseError(error);
	}

	// End of ChildEventListener methods


	public void startWatching() {
		if(!mInitialized) {
			mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
						mSnapshots.add(snapshot);
					}
					notifyChangedListeners(OnChangedListener.EventType.Initialized, -1);

					mQuery.addChildEventListener(FirebaseArrayModel.this);
				}


				@Override
				public void onCancelled(DatabaseError databaseError) {
					FirebaseArrayModel.this.onCancelled(databaseError);
				}
			});
			mInitialized = true;
		} else {
			mQuery.addChildEventListener(this);
		}
	}


	public void stopWatching() {
		mQuery.removeEventListener(this);
	}


	public List<DataSnapshot> getSnapshots() {
		return mSnapshots;
	}


	void setOnChangedListener(OnChangedListener listener) {
		mListener = listener;
	}


	DataSnapshot getSnapshot(int index) {
		return mSnapshots.get(index);
	}


	private void notifyChangedListeners(OnChangedListener.EventType type, int index) {
		notifyChangedListeners(type, index, -1);
	}


	private void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
		if(mListener != null) {
			mListener.onChanged(type, index, oldIndex);
		}
	}


	private int getIndexForKey(String key) {
		int index = 0;
		for(DataSnapshot item : mSnapshots) {
			if(item.getKey().equals(key)) {
				return index;
			} else {
				index++;
			}
		}
		throw new IllegalArgumentException("Key not found");
	}
}
