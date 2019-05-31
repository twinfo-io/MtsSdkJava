/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.rest;

 public interface UnifiedOddsStatisticsMBean {

     int getNumberOfMessagesReceived();

     long getTimeOfLastMessageReceived();

     int getNumberOfRecoveryMessagesReceived();

     int getNumberOfOddsChangesReceived();

     int getNumberOfBetSettlementsReceived();

     int getNumberOfRollbackBetSettlementsReceived();

     int getNumberOfBetCancelsReceived();

     int getNumberOfRollbackBetCancelsReceived();

     int getNumberOfFixtureChangesReceived();

     int getSecondsSinceStart();

     int getNumberOfHttpGetStreaming();

     int getNumberOfHttpGetJaxb();

     String getLastHttpGetURL();

     int getNumberOfCachePurgesDone();

     long getTimeSpentPurgingCaches();

     int getNumberOfLongProcessingTimes();

     long getLongMessageProcessingTimeInMs();

     long getXmlDeserializationTimeInMs();

     int getNumberOfLiveMessages();

     int getNumberOfPrematchMessages();

     long getBytesReceived();
}