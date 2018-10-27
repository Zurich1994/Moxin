package com.heu.moxin.fragment;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;

public abstract class RefreshableFragement extends Fragment {
	private static Map<String, RefreshableFragement> m_registry = new HashMap<String, RefreshableFragement>();

	public RefreshableFragement(){
	}

	public static RefreshableFragement getInstance(String name) {
		name = "com.heu.moxin.fragment." + name;
		if (!m_registry.containsKey(name)) {
			try {
				m_registry.put(name, (RefreshableFragement) Class.forName(name)
						.newInstance());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return m_registry.get(name);
	}

	public abstract void refresh();

	static class SingletonException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
}
