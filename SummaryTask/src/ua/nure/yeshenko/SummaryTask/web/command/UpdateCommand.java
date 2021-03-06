package ua.nure.yeshenko.SummaryTask.web.command;

import static ua.nure.yeshenko.SummaryTask.util.RequestResponceUtil.createForwardResult;
import static ua.nure.yeshenko.SummaryTask.util.RequestResponceUtil.createRedirectResult;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.log4j.Logger;

import ua.nure.yeshenko.SummaryTask.Path;
import ua.nure.yeshenko.SummaryTask.db.ProductDAO;
import ua.nure.yeshenko.SummaryTask.db.entity.Gender;
import ua.nure.yeshenko.SummaryTask.db.entity.Product;
import ua.nure.yeshenko.SummaryTask.db.entity.Type;
import ua.nure.yeshenko.SummaryTask.exception.AppException;
import ua.nure.yeshenko.SummaryTask.exception.Messages;
import ua.nure.yeshenko.SummaryTask.model.RequestResult;

public class UpdateCommand extends Command {
	private static final Logger log = Logger.getLogger(UpdateCommand.class);
	private ProductDAO productDAO;

	public UpdateCommand(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	@Override
	public RequestResult execute(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.debug("Command start");
		HttpSession session = request.getSession();
		long productId;
		try {
			productId = Long.valueOf(request.getParameter("id"));
		} catch (Exception e) {
			log.error(Messages.ERR_REQUEST_ERROR + e);
			throw new AppException(Messages.ERR_REQUEST_ERROR, e);
		}

		Product product = productDAO.findProduct(productId);
		log.trace("Find product in DB -->" + product);

		String name = request.getParameter("name");

		if (name == null || name.isEmpty()) {
			session.setAttribute("mutable", product);
			return createForwardResult(Path.PAGE_UPDATE);
		}

		try {
			product.setName(name);
			product.setPrice(Integer.valueOf(request.getParameter("price")));
			product.setSize(Integer.valueOf(request.getParameter("size")));
			product.setQuantity(Integer.valueOf(request.getParameter("quantity")));
			product.setGender(Gender.values()[Integer.valueOf(request.getParameter("gender"))]);
			product.setType(Type.values()[Integer.valueOf(request.getParameter("type"))]);
			Part file = request.getPart("image");
			if (file.getSize() != 0) {
				byte[] bytesArray = new byte[(int) file.getSize()];
				try (InputStream fis = file.getInputStream()) {
					fis.read(bytesArray);
				}
				product.setImage(new SerialBlob(bytesArray));
			} else {
				product.setImage(null);
			}
			if (product.getPrice() < 0 || product.getSize() < 30 || product.getSize() > 50
					|| product.getQuantity() < 0) {
				throw new AppException(Messages.ERR_REQUEST_ERROR);
			}
		} catch (Exception e) {
			log.error(Messages.ERR_REQUEST_ERROR);
			throw new AppException(Messages.ERR_REQUEST_ERROR, e);
		}

		productDAO.updateProduct(product);
		log.trace("Update product in DB -->" + product);
		log.debug("Command finish");
		return createRedirectResult(Path.COMMAND_CATALOG);
	}

}
