package jp.bs.app.ukiukiview.service;

oneway interface IApiAdapterServiceCallback {
    /*
     * Ordinary results:
     *   responseCode = 1, progress = 0:      "starting"
     *   responseCode = 0, progress = n/a:    "finished"
     *
     * If there is an error, it must be reported as follows:
     *   responseCode = -1, progress = n/a:  "stopping due to error"
     *
     * *Optionally* a callback can also include intermediate values from 1..99 e.g.
     *   responseCode = 1, progress = 0:      "starting"
     *   responseCode = 1, progress = 30:     "working"
     *   responseCode = 1, progress = 60:     "working"
     *   responseCode = 0, progress = n/a:    "finished"
     */
	void loadPoiObjectsCallback(int serviceId, int responseCode, int progress);
	void updateServiceListCallback();
}
