package com.ca.nbiapps.otpimpl;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet;
import com.arcot.logger.ArcotLogger;


@WebServlet(urlPatterns = {"/deliverOtp","/deliverOtpWithChannel","/fetchChannels", "/generateAndSentOtp", "/validateOtp"})
public class SampleOTPListenerAddNew extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String reqURI = req.getRequestURI();
		String crdNumber = null;
		crdNumber = req.getParameter("cardnumber");
		String uniqueID = null;
		uniqueID = req.getParameter("uniqueID");

		if (reqURI.contains("deliverOtpWithChannel")){
			/**
			 *
			 * CA generates OTP, Bank Backend Delivers the OTP & returns the channels to be shown on Pages, CA validates OTP.
			 */
			if (crdNumber.equals("4539490000000351")){
				// [Failed]
				sendResponse(resp, uniqueID == null?"status=failed":"status=failed&uniqueid="+uniqueID);
			}else if (crdNumber.equals("4539490000000369")){
				// [Unavailable]
				sendResponse(resp, uniqueID == null?"status=unknowncard":"status=unknowncard&uniqueid="+uniqueID);
			}else{
				// [Success]
				sendResponse(resp, uniqueID == null?"status=success&mobile=9177*****026,0012*****908&email=san****@c*.com":"status=success&mobile=9177*****026&uniqueid="+uniqueID);
			}

		} else if (reqURI.contains("deliverOtp")){

			/**
			 *
			 * CA generates OTP, Bank Backend Delivers the OTP, CA validates OTP.
			 */
			if (crdNumber.equals("4539490000000054")){
				// bad [Failed]
				sendResponse(resp, uniqueID == null?"status=failed":"status=failed&uniqueid="+uniqueID);
			}else if (crdNumber.equals("4539490000000062")){
				// exception [Unavailable]
				sendResponse(resp, uniqueID == null?"status=unknowncard":"status=unknowncard&uniqueid="+uniqueID);
			}else{
				// success [Success]
				sendResponse(resp, uniqueID == null?"status=success":"status=success&uniqueid="+uniqueID);
			}

		} else if (reqURI.contains("fetchChannels")){

			/**
			 *
			 * CA generates OTP, Bank Backend sends back the channel, CA delivers the OTP, CA validates the OTP.
			 */
			if (crdNumber.equals("4539490000000658")){
				// [Failed]
				sendResponse(resp, uniqueID == null?"status=failed":"status=failed&uniqueid="+uniqueID);
			}else if (crdNumber.equals("4539490000000666")){
				// exception, [Unavailable]
				sendResponse(resp, uniqueID == null?"status=unknowncard":"status=unknowncard&uniqueid="+uniqueID);
			}else{
				// success, with a single channel
				sendResponse(resp, uniqueID == null?"status=success&mobile=919740096454&email=sughi06@ca.com":"status=success&mobile=919740096454&email=sughi06@ca.com&uniqueid="+uniqueID);
			}

		} else if (reqURI.contains("generateAndSentOtp")){

			if (crdNumber.equals("4539490000000955")){
				// [Failed]
				sendResponse(resp, uniqueID == null?"status=failed":"status=failed&uniqueid="+uniqueID);
			}else if (crdNumber.equals("4539490000000963")){
				// exception [Unavailable]
				sendResponse(resp, uniqueID == null?"status=unknowncard":"status=unknowncard&uniqueid="+uniqueID);
			}else{
				// success [Success]
				sendResponse(resp, uniqueID == null?"status=success&mobile=9177*****026":"status=success&mobile=9177*****026&uniqueid="+uniqueID);
			}

		}else if (reqURI.contains("validateOtp")){

			if (crdNumber.equals("4539490000000971")){
				// [Failed]
				sendResponse(resp, uniqueID == null?"status=failed":"status=failed&uniqueid="+uniqueID);
			}else if (crdNumber.equals("4539490000000989")){
				// exception [Unavailable]
				sendResponse(resp, uniqueID == null?"status=unknowncard":"status=unknowncard&uniqueid="+uniqueID);
			}else{
				// success [Success]
				sendResponse(resp, uniqueID == null?"status=success":"status=success&uniqueid="+uniqueID);
			}

		}else{
			ArcotLogger.logInfo("com.ca.nbiapps.otmpimpl.SampleOTPListener::Error in doPost(): Incorrect URI detected. [ " + req.getRequestURI() + "]");
		}

		return;
	}

	private void sendResponse(HttpServletResponse resp, String data) throws IOException {
		OutputStream os = resp.getOutputStream();
		os.write(data.getBytes());
		os.flush();
		return;
	}


}
