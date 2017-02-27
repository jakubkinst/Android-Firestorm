package cz.kinst.jakub.firestorm;

import android.databinding.ObservableField;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


/**
 * Extension of {@link ObservableField} where the value is filled with value from Firebase Realtime Database
 *
 * @param <T> Type of the object being watched
 */
public abstract class ObservableFirebaseModel<T> extends ObservableField<T> implements FirebaseModelListener<T> {

	private FirebaseModel<T> firebaseModel;


	/**
	 * @param firebaseReference Firebase Realtime Database reference (Path to the target object being watched)
	 */
	public ObservableFirebaseModel(DatabaseReference firebaseReference) {
		firebaseModel = new FirebaseModel<T>(firebaseReference) {
			@Override
			protected T parseDataSnapshot(DataSnapshot dataSnapshot) {
				return ObservableFirebaseModel.this.parseDataSnapshot(dataSnapshot);
			}


			@Override
			protected void onDatabaseError(DatabaseError error) {
				ObservableFirebaseModel.this.onDatabaseError(error);
			}
		};
	}


	/**
	 * Implement this method to convert {@link DataSnapshot} instance returned by Firebase to the desired object type (T)
	 * <p>
	 * Note: This method will be called on background thread.
	 */
	protected abstract T parseDataSnapshot(DataSnapshot dataSnapshot);

	/**
	 * Called upon any error returned by Firebase
	 *
	 * @param error
	 */
	protected abstract void onDatabaseError(DatabaseError error);


	@Override
	public void onEntityUpdated(T entity) {
		Log.d("OFM", "Entity updated: " + entity);
		set(entity);
	}


	/**
	 * Starts watching firebase reference - links the {@link com.google.firebase.database.ValueEventListener}
	 * <p>
	 * Usually this should be called in onResume() method within Activity/Fragment/ViewModel
	 */
	public void startWatching() {
		firebaseModel.startWatching();
		firebaseModel.addListener(this);
	}


	/**
	 * Stops watching firebase reference - unlinks the {@link com.google.firebase.database.ValueEventListener}
	 * <p>
	 * Usually this should be called in onPause() method within Activity/Fragment/ViewModel
	 */
	public void stopWatching() {
		firebaseModel.removeListener(this);
		firebaseModel.stopWatching();
	}


	/**
	 * Adds additional custom listener which gets called whenever the value of the watched object is updated
	 */
	public void addListener(FirebaseModelListener<T> listener) {
		firebaseModel.addListener(listener);
	}


	/**
	 * Removes additional custom listener
	 */
	public void removeListener(FirebaseModelListener<T> listener) {
		firebaseModel.removeListener(listener);
	}

}
