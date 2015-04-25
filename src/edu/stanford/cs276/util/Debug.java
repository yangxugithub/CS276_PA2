package edu.stanford.cs276.util;

public class Debug {
	boolean dev=false;
	

	public Debug(){
	
	}
	public void test( String msg ){
		if (!dev){
			System.out.println(msg);
		}
	}
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(String devOrTest) throws Exception {
		if (devOrTest.equals("dev") ){
			this.dev = true;
		} 
		else if (devOrTest.equals("test")) {
			this.dev = false;
		}
		else{
			throw new Exception ("Invalid dev/test argument");
		}

	}
	public void dev(String msg) {
		if (dev){
			System.out.println(msg);
		}
	}
}
