package cz.kinst.jakub.sample.firestorm;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

	private MainViewModel viewModel;
	private ViewDataBinding binding;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewModel = new MainViewModel();

		binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
		binding.setVariable(BR.viewModel, viewModel);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.menu_add) {
			final EditText input = new EditText(this);
			input.setHint(R.string.add_hint);
			input.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);

			new AlertDialog.Builder(this)
					.setTitle(R.string.add_title)
					.setView(input)
					.setPositiveButton(R.string.add, (dialog, whichButton) -> {
						String title = input.getText().toString();
						viewModel.addToDo(new ToDoItem(title));
					})
					.setNegativeButton(R.string.cancel, (dialog, whichButton) -> {
					})
					.show();
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onResume() {
		super.onResume();
		viewModel.onResume();
	}


	@Override
	protected void onPause() {
		viewModel.onPause();
		super.onPause();
	}
}
