package getMyIP;

import java.util.concurrent.ConcurrentHashMap;

public class StatisticsStore {
	private String TOTAL_CHECKS = "1";
	private String TOTAL_FAILURES = "2";
	private String TOTAL_CHANGES = "3";
	
	private ConcurrentHashMap<String, Integer> store = new ConcurrentHashMap<String, Integer>();
	private String currentIp;
	
	public StatisticsStore() {
		store.put(TOTAL_CHECKS, 0);		
		store.put(TOTAL_FAILURES, 0);
		store.put(TOTAL_CHANGES, 0);
	}

	public Integer getTotalChecks() {
		return store.get(TOTAL_CHECKS);
	}

	public Integer getTotalFailures() {
		return store.get(TOTAL_FAILURES);
	}

	public Integer getTotalChanges() {
		return store.get(TOTAL_CHANGES);
	}
	
	
	public void incrementCheck() {
		Integer integer = store.get(TOTAL_CHECKS);
		store.put(TOTAL_CHECKS, ++integer);
	}

	public void incrementFailure() {
		Integer integer = store.get(TOTAL_FAILURES);
		store.put(TOTAL_FAILURES, ++integer);
	}
	
	public void incrementChange() {
		Integer integer = store.get(TOTAL_CHANGES);
		store.put(TOTAL_CHANGES, ++integer);
	}

	public String getCurrentIp() {
		return currentIp;
	}

	public void setCurrentIp(String myIp) {
		currentIp = myIp;
	}
}
