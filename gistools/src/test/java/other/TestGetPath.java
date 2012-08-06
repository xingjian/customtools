/**@Title: TestGetPath.java @author promisePB xingjian@yeah.net @date 2010-12-8 上午09:03:31 */

package other;

/**   
 * @Title: TestGetPath.java 
 * @Package com.xingjian.cn.test 
 * @Description: 测试获取路径问题
 * @author promisePB xingjian@yeah.net   
 * @date 2010-12-8 上午09:03:31 
 * @version V1.0   
 */

public class TestGetPath {

	/**
	 * 获取路径
	 */
	public void getClassPath(){
		System.out.println(this.getClass().getClassLoader().getResource("").getPath());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestGetPath tgp = new TestGetPath();
		tgp.getClassPath();
	}

}
