package br.ufrn.sd.project2.util;

import javafx.application.Platform;

/**
 *
 * https://github.com/victorlaerte/javafx-asynctask/tree/master/src/main/java/com/victorlaerte/asynctask
 */
public abstract class AsyncTask {

	private boolean daemon = true;

	public abstract void onPreExecute();

	public abstract void doInBackground() throws Exception;

	public abstract void onPostExecute();
	
	public abstract void onFail(Exception e);

	public abstract void progressCallback(Object... params);

	public void publishProgress(final Object... params) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				progressCallback(params);
			}
		});
	}

	private final Thread backGroundThread = new Thread(new Runnable() {

		@Override
		public void run() {
			try {
				doInBackground();

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						onPostExecute();
					}
				});
			} catch(final Exception e) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						onFail(e);
					}
				});
				
			}
		}
	});

	public void execute() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				onPreExecute();

				backGroundThread.setDaemon(daemon);
				backGroundThread.start();
			}
		});
	}

	public void setDaemon(boolean daemon) {

		this.daemon = daemon;
	}

	public void interrupt() {

		this.backGroundThread.interrupt();
	}
}