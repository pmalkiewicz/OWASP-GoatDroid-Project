/**
 * OWASP GoatDroid Project
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * GoatDroid project. For details, please see
 * https://www.owasp.org/index.php/Projects/OWASP_GoatDroid_Project
 *
 * Copyright (c) 2012 - The OWASP Foundation
 * 
 * GoatDroid is published by OWASP under the GPLv3 license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jack Mannino (Jack.Mannino@owasp.org https://www.owasp.org/index.php/User:Jack_Mannino)
 * @created 2012
 */
package org.owasp.goatdroid.webservice.herdfinancial.resource;

import javax.ws.rs.CookieParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.owasp.goatdroid.webservice.herdfinancial.Constants;
import org.owasp.goatdroid.webservice.herdfinancial.bean.StatementBean;
import org.owasp.goatdroid.webservice.herdfinancial.impl.Statement;

@Path("/herdfinancial/api/v1/statements")
public class StatementResource {

	@GET
	@Produces("application/json")
	@Path("get_statement/{accountNumber}/{startDate}/{endDate}")
	public StatementBean getStatement(
			@PathParam("accountNumber") String accountNumber,
			@PathParam("startDate") String startDate,
			@PathParam("endDate") String endDate,
			@CookieParam(Constants.SESSION_TOKEN) int sessionToken) {
		try {
			return Statement.getStatement(accountNumber, startDate, endDate,
					sessionToken);
		} catch (NullPointerException e) {
			StatementBean bean = new StatementBean();
			bean.setSuccess(false);
			return bean;
		}
	}

	@GET
	@Produces("application/json")
	@Path("poll_statement_updates/{accountNumber}")
	public StatementBean getStatementSinceLastPoll(
			@PathParam("accountNumber") String accountNumber,
			@CookieParam(Constants.SESSION_TOKEN) int sessionToken) {
		try {
			return Statement.getStatementSinceLastPoll(accountNumber,
					sessionToken);
		} catch (NullPointerException e) {
			StatementBean bean = new StatementBean();
			bean.setSuccess(false);
			return bean;
		}
	}
}
