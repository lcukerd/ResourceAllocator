package ResourceAllocator.Model;

public class Region {

  private String region;
  private double large;
  private double xlarge;
  private double _2xlarge;
  private double _4xlarge;
  private double _8xlarge;
  private double _10xlarge;

  public Region(String region, double large, double xlarge, double _2xlarge, double _4xlarge, double _8xlarge, double _10xlarge) {
    this.region = region;
    this.large = large;
    this.xlarge = xlarge;
    this._2xlarge = _2xlarge;
    this._4xlarge = _4xlarge;
    this._8xlarge = _8xlarge;
    this._10xlarge = _10xlarge;
  }
  
  public String getRegion() {
	  return region;
  }

  public double getLarge() {
    return large;
  }

  public double getXlarge() {
    return xlarge;
  }

  public double get2xlarge() {
    return _2xlarge;
  }

  public double get4xlarge() {
    return _4xlarge;
  }

  public double get8xlarge() {
    return _8xlarge;
  }

  public double get10xlarge() {
    return _10xlarge;
  }
}
