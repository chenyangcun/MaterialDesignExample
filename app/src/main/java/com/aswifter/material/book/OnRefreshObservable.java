package com.aswifter.material.book;

import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.agera.BaseObservable;

public class OnRefreshObservable extends BaseObservable
    implements SwipeRefreshLayout.OnRefreshListener {

  /**
   * Triggered when the associated {@link SwipeRefreshLayout} is refreshed by the user. The event
   * is passed on to the observers, using the {@link UpdateDispatcher} provided by {@link
   * BaseObservable}.
   */
  @Override
  public void onRefresh() {
    dispatchUpdate();
  }
}