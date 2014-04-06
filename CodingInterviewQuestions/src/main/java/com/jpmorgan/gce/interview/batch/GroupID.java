package com.jpmorgan.gce.interview.batch;

public class GroupID {

    private final String groupCode;
    private final int groupLevel;

    public GroupID(String groupCode, int groupLevel) {
        this.groupCode = groupCode;
        this.groupLevel = groupLevel;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public int getGroupLevel() {
        return groupLevel;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupCode == null) ? 0 : groupCode.hashCode());
		result = prime * result + groupLevel;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupID other = (GroupID) obj;
		if (groupCode == null) {
			if (other.groupCode != null)
				return false;
		} else if (!groupCode.equals(other.groupCode))
			return false;
		if (groupLevel != other.groupLevel)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GroupID [groupCode=" + groupCode + ", groupLevel=" + groupLevel + "]";
	}

    
}
