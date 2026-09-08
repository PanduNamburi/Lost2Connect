package com.lost2found.dto;

/**
 * Data Transfer Object for Dashboard Stats & Metrics.
 */
public class DashboardStatsResponse {

    private long totalReports;
    private long myReports;
    private long matches;
    private long itemsReunited;
    private long communityMembers;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalReports, long myReports, long matches, long itemsReunited, long communityMembers) {
        this.totalReports = totalReports;
        this.myReports = myReports;
        this.matches = matches;
        this.itemsReunited = itemsReunited;
        this.communityMembers = communityMembers;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getMyReports() {
        return myReports;
    }

    public void setMyReports(long myReports) {
        this.myReports = myReports;
    }

    public long getMatches() {
        return matches;
    }

    public void setMatches(long matches) {
        this.matches = matches;
    }

    public long getItemsReunited() {
        return itemsReunited;
    }

    public void setItemsReunited(long itemsReunited) {
        this.itemsReunited = itemsReunited;
    }

    public long getCommunityMembers() {
        return communityMembers;
    }

    public void setCommunityMembers(long communityMembers) {
        this.communityMembers = communityMembers;
    }
}
