package com.alcatel.smartlinkv3.business.model;

public class UsageDataMode
{
    private double usageData;
    private int usageUnit;//0:mb 1:gb
   
    public double getUsageData()
    {
        return usageData;
    }
    public void setUsageData(double usageData)
    {
        this.usageData = usageData;
    }
    public int getUsageUnit()
    {
        return usageUnit;
    }
    public void setUsageUnit(int usageUnit)
    {
        this.usageUnit = usageUnit;
    }


}
