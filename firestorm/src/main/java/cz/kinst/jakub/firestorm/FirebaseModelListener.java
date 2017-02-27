package cz.kinst.jakub.firestorm;

public interface FirebaseModelListener<T> {
	void onEntityUpdated(T entity);
}
