package ua.nure.yeshenko.SummaryTask.web.command;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ua.nure.yeshenko.SummaryTask.Path;
import ua.nure.yeshenko.SummaryTask.db.ProductDAO;
import ua.nure.yeshenko.SummaryTask.db.entity.Product;
import ua.nure.yeshenko.SummaryTask.exception.AppException;
import ua.nure.yeshenko.SummaryTask.exception.Messages;
import ua.nure.yeshenko.SummaryTask.model.RequestResult;
import static ua.nure.yeshenko.SummaryTask.util.RequestResponceUtil.createForwardResult;

public class SearchCommand extends Command {
	private static final Logger log = Logger.getLogger(SearchCommand.class);
	private ProductDAO productDAO;

	public SearchCommand(ProductDAO productDAO) {
		super();
		this.productDAO = productDAO;
	}

	@Override
	public RequestResult execute(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.debug("Command start");

		String name = request.getParameter("name");
		if (name == null || name.isEmpty()) {
			log.error(Messages.ERR_REQUEST_ERROR);
			throw new AppException(Messages.ERR_REQUEST_ERROR);
		}

		List<Product> products = productDAO.findProduct(name);
		HttpSession session = request.getSession();
		session.setAttribute("products", products);
		log.trace("Set the session attribute: products" + products);
		session.setAttribute("searchResult", true);

		log.debug("Command finish");
		return createForwardResult(Path.PAGE_CATALOG);
	}

}
