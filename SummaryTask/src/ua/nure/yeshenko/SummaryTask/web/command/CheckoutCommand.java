package ua.nure.yeshenko.SummaryTask.web.command;

import static ua.nure.yeshenko.SummaryTask.util.ProccessUtil.createRedirectResult;
import static ua.nure.yeshenko.SummaryTask.util.ProccessUtil.createForwardResult;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ua.nure.yeshenko.SummaryTask.Path;
import ua.nure.yeshenko.SummaryTask.db.Role;
import ua.nure.yeshenko.SummaryTask.db.bean.CartBean;
import ua.nure.yeshenko.SummaryTask.db.entity.User;
import ua.nure.yeshenko.SummaryTask.exception.AppException;
import ua.nure.yeshenko.SummaryTask.exception.Messages;
import ua.nure.yeshenko.SummaryTask.model.ProcessResult;

public class CheckoutCommand extends Command{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8901259669236702211L;
	private static final Logger log = Logger.getLogger(CheckoutCommand.class);

	@Override
	public ProcessResult execute() throws IOException, ServletException, AppException {
		log.debug("Command start");
		HttpSession session = request.getSession();
		
		User user = (User) session.getAttribute("user");
		
		if(user == null) {
			return createRedirectResult(Path.PAGE_LOGIN);
		}
		
		if(session.getAttribute("userRole") == Role.BLOCKED) {
			log.error(Messages.ERR_USER_IS_BLOCKED);
			throw new AppException(Messages.ERR_USER_IS_BLOCKED);
		}
		
		if(CartBean.get(session).getCart().isEmpty()) {
			session.setAttribute("isEmptyCart", true);
			return createRedirectResult(Path.COMMAND__CART);
		}
		
		session.setAttribute("city", user.getCity());
		log.debug("Command finish");
		return createForwardResult(Path.PAGE__CHECKOUT);
	}

}