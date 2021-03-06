package goods.admin.web.servlet;

import java.io.IOException;

import goods.order.domain.Order;
import goods.order.service.OrderService;
import goods.page.PageBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servlet.BaseServlet;

public class AdminOrderServlet extends BaseServlet{
	private OrderService orderService=new OrderService();
	
	/*
	 * 获取当前页码
	 */
	private int getPc(HttpServletRequest req){
		int currentPage=1;
		String param=req.getParameter("currentPage");
		if(param!=null && !param.trim().isEmpty()){
			try{
				currentPage=Integer.parseInt(param);
			}catch (RuntimeException e){}
		}
		return currentPage;
	}
	
	/*
	 * 截取url，作为页面中的分页导航中超链接的目标！
	 *
	 * http://localhost:8080/goods/BookServlet?methed=findByCategory&cid=xxx&pc=3
	 * /goods/BookServlet + methed=findByCategory&cid=xxx&pc=3
	 */
	private String getUrl(HttpServletRequest req){
		String url= req.getRequestURI()+"?"+req.getQueryString();
		
		int index=url.lastIndexOf("currentPage");
		if(index!=-1){
			url.substring(0,index);
		}
		return url;
	}
		
	/**
	 * 查看所有订单
	 */
	public String findAll(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getUrl(req);
		
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Order> pb = orderService.findAll(pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		//System.out.println("+++++++++++++++++"+pb+"====================");
		//return null;
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 按状态查询
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getUrl(req);
		/*
		 * 3. 获取链接参数：status
		 */
		int status = Integer.parseInt(req.getParameter("status"));
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Order> pb = orderService.findByStatus(status, pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 查看订单详细信息
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.loadOrder(oid);
		req.setAttribute("order", order);
		//System.out.println("++++++++++++++"+order.getAddress()+order.getOwner()+"++++++++++++++++");
		String btn = req.getParameter("btn");//btn说明了用户点击哪个超链接来访问本方法的
		req.setAttribute("btn", btn);
		return "/adminjsps/admin/order/desc.jsp";
	}
	
	/**
	 * 取消订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findOrderStatus(oid);
		if(status != 1) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，不能取消！");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5);//设置状态为取消！
		req.setAttribute("code", "success");
		req.setAttribute("msg", "该订单已取消！");
		return "f:/adminjsps/msg.jsp";		
	}
	
	/**
	 * 发货功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findOrderStatus(oid);
		if(status != 2) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，不能发货！");
			return "f:/adminjsps/msg.jsp";
		}
		orderService.updateStatus(oid, 3);//设置状态为取消！
		req.setAttribute("code", "success");
		req.setAttribute("msg", "该订单已发货！请等待收货！");
		return "f:/adminjsps/msg.jsp";		
	}
}

