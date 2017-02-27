package cz.kinst.jakub.firestorm;

import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.util.ArraySet;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;


abstract class FirebaseModel<T> implements ValueEventListener {


	private DatabaseReference firebaseReference;
	private T lastValue = null;
	private Set<FirebaseModelListener<T>> listeners = new ArraySet<>();


	FirebaseModel(DatabaseReference firebaseReference) {
		this.firebaseReference = firebaseReference;
	}


	protected abstract T parseDataSnapshot(DataSnapshot dataSnapshot);
	protected abstract void onDatabaseError(DatabaseError error);


	@Override
	public void onCancelled(DatabaseError error) {
		onDatabaseError(error);
	}


	@Override
	public void onDataChange(DataSnapshot dataSnapshot) {
		if(dataSnapshot != null)
			AsyncTaskCompat.executeParallel(new ParsingTask(), dataSnapshot);
		else
			lastValue = null;
	}


	public void startWatching() {
		firebaseReference.addValueEventListener(this);
	}


	public void stopWatching() {
		firebaseReference.removeEventListener(this);
	}


	void addListener(FirebaseModelListener<T> listener) {
		listeners.add(listener);
		if(lastValue != null)
			listener.onEntityUpdated(lastValue);
	}


	void removeListener(FirebaseModelListener<T> listener) {
		listeners.remove(listener);
	}


	private void onDataSnapshotParsed(T value) {
		lastValue = value;
		for(FirebaseModelListener<T> listener : listeners)
			listener.onEntityUpdated(lastValue);
	}


	private class ParsingTask extends AsyncTask<DataSnapshot, Void, T> {
		@Override
		protected T doInBackground(DataSnapshot... dataSnapshot) {
			return parseDataSnapshot(dataSnapshot[0]);
		}


		@Override
		protected void onPostExecute(T entity) {
			onDataSnapshotParsed(entity);
		}
	}
}
