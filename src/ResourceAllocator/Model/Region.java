package ResourceAllocator.Model;

public class Region {

  private String region;
  private float large;
  private float xlarge;
  private float _2xlarge;
  private float _4xlarge;
  private float _8xlarge;
  private float _10xlarge;

  public Region(String region, float large, float xlarge, float _2xlarge, float _4xlarge, float _8xlarge, float _10xlarge) {
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

  public float getLarge() {
    return large;
  }

  public float getXlarge() {
    return xlarge;
  }

  public float get2xlarge() {
    return _2xlarge;
  }

  public float get4xlarge() {
    return _4xlarge;
  }

  public float get8xlarge() {
    return _8xlarge;
  }

  public float get10xlarge() {
    return _10xlarge;
  }
}
