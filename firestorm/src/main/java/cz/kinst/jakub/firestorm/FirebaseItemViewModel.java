package cz.kinst.jakub.firestorm;

import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import com.google.firebase.database.DataSnapshot;


/**
 * ItemViewModel wrapping individual item models.
 * Responsible for parsing {@link DataSnapshot} returned from Firebase which is done on a background thread
 *
 * @param <T>
 */
public abstract class FirebaseItemViewModel<T> extends ItemViewModel {


	private final ObservableField<T> item = new ObservableField<>();


	protected FirebaseItemViewModel(DataSnapshot dataSnapshot) {
		AsyncTaskCompat.executeParallel(new ParsingTask(), dataSnapshot);
	}


	/**
	 * Implement this method to convert {@link DataSnapshot} instance returned by Firebase to the desired item object type (T)
	 * <p>
	 * Note: This method will be called on background thread.
	 */
	public abstract T parseDataSnapshot(DataSnapshot dataSnapshot);


	/**
	 * Get wrapped item model
	 */
	public ObservableField<T> getItem() {
		return item;
	}


	/**
	 * Called when item model is loaded and parsed
	 */
	protected void onItemLoaded() {

	}


	private class ParsingTask extends AsyncTask<DataSnapshot, Void, T> {

		@Override
		protected T doInBackground(DataSnapshot... dataSnapshots) {
			return parseDataSnapshot(dataSnapshots[0]);
		}


		@Override
		protected void onPostExecute(T t) {
			item.set(t);
			onItemLoaded();
		}
	}
}
