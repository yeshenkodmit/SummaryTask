package ua.nure.yeshenko.SummaryTask.web.command;

import static ua.nure.yeshenko.SummaryTask.util.RequestResponceUtil.createForwardResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ua.nure.yeshenko.SummaryTask.Path;
import ua.nure.yeshenko.SummaryTask.db.UserDAO;
import ua.nure.yeshenko.SummaryTask.db.entity.Role;
import ua.nure.yeshenko.SummaryTask.db.entity.User;
import ua.nure.yeshenko.SummaryTask.exception.AppException;
import ua.nure.yeshenko.SummaryTask.exception.Messages;
import ua.nure.yeshenko.SummaryTask.model.RequestResult;

public class ListClientsCommand extends Command {
	private static final Logger log = Logger.getLogger(ListClientsCommand.class);
	private UserDAO userDAO;

	public ListClientsCommand(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public RequestResult execute(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.debug("Command start");
		HttpSession session = request.getSession();

		User user = (User) session.getAttribute("user");
		log.trace("Get request parameter: user --> " + user);

		if (Role.getRole(user) != Role.ADMIN) {
			log.error(Messages.ERR_REQUEST_ERROR);
			throw new AppException(Messages.ERR_REQUEST_ERROR);
		}

		List<User> blackList = new ArrayList<>();
		List<User> whiteList = new ArrayList<>();

		for (User users : userDAO.findAllUsers()) {
			if (Role.getRole(users) == Role.BLOCKED) {
				blackList.add(users);
			} else {
				whiteList.add(users);
			}
		}

		session.setAttribute("whiteList", whiteList);
		log.trace("Set the session attribute: whiteList --> " + whiteList);
		session.setAttribute("blackList", blackList);
		log.trace("Set the session attribute: blackList --> " + blackList);
		log.debug("Command finish");
		return createForwardResult(Path.PAGE_LIST_CLIENTS);
	}

}
