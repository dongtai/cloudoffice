package apps.transmanager.weboffice.service.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import apps.transmanager.weboffice.databaseobject.MailAccount;
import apps.transmanager.weboffice.databaseobject.MailAccountSign;
import apps.transmanager.weboffice.databaseobject.MailFolders;
import apps.transmanager.weboffice.databaseobject.MailMessages;
import apps.transmanager.weboffice.databaseobject.MailSources;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.dao.NormalDAO;
import apps.transmanager.weboffice.service.mail.commailconfig.CommonMailConfig;

@Component(value = MailService.NAME)
public class MailService {
	public static final String NAME = "mailService";

	@Autowired
	private NormalDAO normalDAO;

	// //////////////////////////////////////////////////////
	public List<MailAccount> findALLbyUser(Users user) {
		String queryString = "from MailAccount as model where model.user = ? order by model.isdefault desc";
		List<MailAccount> lists = normalDAO.findAllBySql(queryString, user);
		return lists;
	}

	public List<Object[]> findALLByUser(Users user) {
		String queryString = "select id,email,isdefault from "+" MailAccount as model where model.user = ? order by model.isdefault desc";
		List<Object[]> lists = normalDAO.findAllBySql(queryString, user);
		return lists;
	}
	
	public List<MailAccount> findALLbyUser(Users user, String email) {
		if (email == null) {
			return findALLbyUser(user);
		}
		String queryString = "from MailAccount as model where model.user = ? and model.email = ?";
		List<MailAccount> lists = normalDAO.findAllBySql(queryString, user,
				email);
		return lists;
	}
	
	public MailAccount findmailbyUser(Users user, long id) {
		MailAccount ma = null;
		if (id == 0) {
			return ma;
		}
		String queryString = "from MailAccount as model where model.user = ? and model.id = ?";
		List<MailAccount> lists = normalDAO.findAllBySql(queryString, user,
				id);
		if(lists.size()!=0)
			ma = lists.get(0);
		return ma;
	}

	public MailAccount finddefaultAccount(Users user) {
		List<MailAccount> lists = normalDAO.findByProperty(MailAccount.class,
				"user", user);
		for (MailAccount mailAccount : lists) {
			if (mailAccount.getIsdefault()) {
				return mailAccount;
			}
		}
		return null;
	}

	public MailAccount findinnerAccount(Users user) {
		List<MailAccount> lists = normalDAO.findByProperty(MailAccount.class,
				"user", user);
		for (MailAccount mailAccount : lists) {
			if (mailAccount.getIsinnerAccount()) {
				return mailAccount;
			}
		}
		return null;
	}

	public void createMailAccount(MailAccount ma) {
		normalDAO.save(ma);
	}

	public void updateMailAccount(MailAccount ma) {
		normalDAO.update(ma);
	}

	public void deleteMailAccount(Long id) {
		normalDAO.deleteEntityByID(MailAccount.class, "id", id);
	}

	public MailAccount findMailAccountById(Long id) {
		return (MailAccount) normalDAO.find(MailAccount.class, id);
	}

	// //////////////////////////////////////////////////////////////////////////
	private boolean isInited() {
		List list = normalDAO.findAll("MailFolders");
		if (list == null || list.size() <= 0) {
			return false;
		}
		return true;
	}

	public void initData()// 初始化邮件信息。Folder
	{
		if (isInited()) {

			String queryString = "from MailFolders as model where model.name = ? and model.parent = ?";
			MailFolders local = (MailFolders) normalDAO
					.findOneObjectBySql(
							"from MailFolders as model where model.name = ?and model.parent is null",
							MailFolders.LOCAL);
			MailFolders.LOCAL_ID = local.getId();
			MailFolders mf = (MailFolders) normalDAO.findOneObjectBySql(
					queryString, MailFolders.INBOX, local);
			MailFolders.INBOX_ID = mf.getId();
			mf = (MailFolders) normalDAO.findOneObjectBySql(queryString,
					MailFolders.SENT, local);
			MailFolders.SENT_ID = mf.getId();
			mf = (MailFolders) normalDAO.findOneObjectBySql(queryString,
					MailFolders.DRAFT, local);
			MailFolders.DRAFT_ID = mf.getId();
			mf = (MailFolders) normalDAO.findOneObjectBySql(queryString,
					MailFolders.TRASH, local);
			MailFolders.TRASH_ID = mf.getId();
			mf = (MailFolders) normalDAO.findOneObjectBySql(queryString,
					MailFolders.OUTBOX, local);
			MailFolders.OUTBOX_ID = mf.getId();
			return;
		}
		MailFolders local = new MailFolders();
		local.setName(MailFolders.LOCAL);
		// local.setId(MailFolders.LOCAL_ID);
		normalDAO.save(local);
		MailFolders.LOCAL_ID = local.getId();

		MailFolders mf = new MailFolders();
		// mf.setId(MailFolders.INBOX_ID);
		mf.setAccount(null);
		mf.setName(MailFolders.INBOX);
		mf.setParent(local);
		normalDAO.save(mf);
		MailFolders.INBOX_ID = mf.getId();

		mf = new MailFolders();
		// mf.setId(MailFolders.SENT_ID);
		mf.setAccount(null);
		mf.setName(MailFolders.SENT);
		mf.setParent(local);
		normalDAO.save(mf);
		MailFolders.SENT_ID = mf.getId();

		mf = new MailFolders();
		// mf.setId(MailFolders.DRAFT_ID);
		mf.setAccount(null);
		mf.setName(MailFolders.DRAFT);
		mf.setParent(local);
		normalDAO.save(mf);
		MailFolders.DRAFT_ID = mf.getId();

		mf = new MailFolders();
		// mf.setId(MailFolders.TRASH_ID);
		mf.setAccount(null);
		mf.setName(MailFolders.TRASH);
		mf.setParent(local);
		normalDAO.save(mf);
		MailFolders.TRASH_ID = mf.getId();

		mf = new MailFolders();
		// mf.setId(MailFolders.OUTBOX_ID);
		mf.setAccount(null);
		mf.setName(MailFolders.OUTBOX);
		mf.setParent(local);
		normalDAO.save(mf);
		MailFolders.OUTBOX_ID = mf.getId();
	}

	public MailFolders findMailFolderById(Long id) {
		return (MailFolders) normalDAO.find(MailFolders.class, id);
	}

	public void saveMailSources(MailSources entities) {
		try {
			normalDAO.save(entities);
		} catch (Exception e) {
		}
	}

	public void saveMailMessages(Collection<MailMessages> entities) {
		normalDAO.saveAll(entities);
	}

	public void saveMailMessage(MailMessages entities) {
		normalDAO.save(entities);
	}

	public void updateMailMessage(MailMessages entities) {
		normalDAO.update(entities);
	}

	public void delMailMessage(MailMessages entities) {
		normalDAO.deleteEntityByID(MailMessages.class, "id", entities.getId());
	}

	public void delMailMessages(List<MailMessages> entities) {
		if (entities.size() == 0) {
			return;
		}
		ArrayList<Long> lists = new ArrayList<Long>(entities.size());
		for (MailMessages mail : entities) {
			lists.add(mail.getId());
		}
		normalDAO.deleteEntityByID(MailMessages.class, "id", lists);
	}

	public void delMailSource(MailSources entities) {
		normalDAO.delete(entities);
	}

	public List<MailMessages> findAllMailMessages(MailAccount ma, MailFolders mf) {
		String queryString = "from MailMessages as model where model.account = ? and model.folder = ?";
		List<MailMessages> mms = normalDAO.findAllBySql(queryString, ma, mf);
		return mms;
	}
	
	public MailMessages findMailMessage(MailAccount ma, Long  id) {
		MailMessages mm = null;
		if(id == null)
			return mm;
		String queryString = "from MailMessages as model where model.account = ? and model.id = ?";
		List<MailMessages> mms = normalDAO.findAllBySql(queryString, ma, id);
		if(mms.size()!=0)
			mm = mms.get(0);
		return mm;
	}

	public List<MailMessages> findMailMessages(MailAccount ma, MailFolders mf,
			String sort, int start, int length) {
		String queryString = "from MailMessages as model where model.account = ? and model.folder = ?";
		if (sort != null && sort.length() != 0) {
			queryString += (" order by model." + sort);// from asc or form desc
														// 默认 receivedDate desc
		} else {
			queryString += " order by model.sentDate desc";
		}
		List<MailMessages> mms = normalDAO.findAllBySql(start, length,
				queryString, ma, mf);
		return mms;
	}
	
	public List<Object[]> findMailMessage(MailAccount ma, MailFolders mf,
			String sort, int start, int length) {
		String queryString = "select isseen,id,hasatt,mto,mfrom,subject,sentDate,msgSize,cc,bcc from" + " MailMessages as model where model.account = ? and model.folder = ?";
		if (sort != null && sort.length() != 0) {
			queryString += (" order by model." + sort);// from asc or form desc
														// 默认 receivedDate desc
		} else {
			queryString += " order by model.sentDate desc";
		}
		List<Object[]> mms = normalDAO.findAllBySql(start, length,
				queryString, ma, mf);
		return mms;
	}
	
	public List<Object[]> findMailmessages(MailAccount ma, MailFolders mf,
			String sort, int start, int length) {
		String queryString = "select id,mfrom,subject,sentDate from" + " MailMessages as model where model.account = ? and model.folder = ?";
		if (sort != null && sort.length() != 0) {
			queryString += (" order by model." + sort);// from asc or form desc
														// 默认 receivedDate desc
		} else {
			queryString += " order by model.sentDate desc";
		}
		List<Object[]> mms = normalDAO.findAllBySql(start, length,
				queryString, ma, mf);
		return mms;
	}

	public long countMailMessages(MailAccount ma, MailFolders mf) {
		final String queryString = "select count(*) from "
				+ "MailMessages as model where model.account = ? and model.folder = ?";
		return normalDAO.getCountBySql(queryString, ma, mf);
	}

	public long countMailUnSeenMessages(MailAccount ma, MailFolders mf) {
		final String queryString = "select count(*) from "
				+ "MailMessages as model where model.account = ? and model.folder = ?"
				+ "and model.isseen = ?";
		return normalDAO.getCountBySql(queryString, ma, mf, Boolean.FALSE);
	}

	public MailMessages findMailMessageById(Long id) {
		return (MailMessages) normalDAO.find(MailMessages.class, id);
	}

	public MailMessages findMailMessageByUId(MailAccount ma, String uid) {
		String queryString = "from MailMessages as model where model.account = ? and model.UID = ?";
		return (MailMessages) normalDAO
				.findOneObjectBySql(queryString, ma, uid);
	}

	public List<MailMessages> searchMailMessages(MailAccount ma, String sort,
			int start, int length, MailFolders mf, String subject,
			String mfrom, String mto, Boolean hasatt, Date startdate,
			Date enddate) {

		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(ma);
		String queryString = "from MailMessages as model where model.account = ?";
		if (mf != null) {
			queryString += "and model.folder = ?";
			objs.add(mf);
		}
		if (subject != null) {
			queryString += "and model.subject like ?";
			objs.add('%' + subject + '%');
		}
		if (mfrom != null) {
			queryString += "and model.mfrom like ?";
			objs.add('%' + mfrom + '%');
		}
		if (mto != null) {
			queryString += "and model.mto like ?";
			objs.add('%' + mto + '%');
		}
		if (hasatt != null) {
			queryString += "and model.hasatt = ?";
			objs.add(hasatt);
		}
		if (startdate != null && enddate != null) {
			queryString += "and model.sentDate between ? and ?";
			objs.add(startdate);
			objs.add(enddate);
		}
		if (startdate != null && enddate == null) {
			queryString += "and model.sentDate > ?";
			objs.add(startdate);
		}
		if (startdate == null && enddate != null) {
			queryString += "and model.sentDate < ?";
			objs.add(enddate);
		}
		if (sort != null && sort.length() != 0) {
			queryString += (" order by model." + sort);// from asc or form desc
														// 默认 receivedDate desc
		} else {
			queryString += " order by model.sentDate desc";
		}
		List<MailMessages> mms = normalDAO.findAllBySql(start, length,
				queryString, objs.toArray());
		return mms;
	}

	
	public List<Object[]> searchMailMessage(MailAccount ma, String sort,
			int start, int length, MailFolders mf, String subject,
			String mfrom, String mto, Boolean hasatt, Date startdate,
			Date enddate) {

		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(ma);
		String queryString = "select isseen,id,hasatt,mto,mfrom,subject,sentDate,msgSize,cc,bcc from" + " MailMessages as model where model.account = ?";
		if (mf != null) {
			queryString += "and model.folder = ?";
			objs.add(mf);
		}
		if (subject != null) {
			queryString += "and model.subject like ?";
			objs.add('%' + subject + '%');
		}
		if (mfrom != null) {
			queryString += "and model.mfrom like ?";
			objs.add('%' + mfrom + '%');
		}
		if (mto != null) {
			queryString += "and model.mto like ?";
			objs.add('%' + mto + '%');
		}
		if (hasatt != null) {
			queryString += "and model.hasatt = ?";
			objs.add(hasatt);
		}
		if (startdate != null && enddate != null) {
			queryString += "and model.sentDate between ? and ?";
			objs.add(startdate);
			objs.add(enddate);
		}
		if (startdate != null && enddate == null) {
			queryString += "and model.sentDate > ?";
			objs.add(startdate);
		}
		if (startdate == null && enddate != null) {
			queryString += "and model.sentDate < ?";
			objs.add(enddate);
		}
		if (sort != null && sort.length() != 0) {
			queryString += (" order by model." + sort);// from asc or form desc
														// 默认 receivedDate desc
		} else {
			queryString += " order by model.sentDate desc";
		}
		List<Object[]> mms = normalDAO.findAllBySql(start, length,
				queryString, objs.toArray());
		return mms;
	}
	
	public Long searchcountMailMessages(MailAccount ma, MailFolders mf,
			String subject, String mfrom, String mto, Boolean hasatt,
			Date startdate, Date enddate) {
		ArrayList<Object> objs = new ArrayList<Object>();
		objs.add(ma);
		String queryString = "select count(*) from MailMessages as model where model.account = ?";
		if (mf != null) {
			queryString += "and model.folder = ?";
			objs.add(mf);
		}
		if (subject != null) {
			queryString += "and model.subject like ?";
			objs.add('%' + subject + '%');
		}
		if (mfrom != null) {
			queryString += "and model.mfrom like ?";
			objs.add('%' + mfrom + '%');
		}
		if (mto != null) {
			queryString += "and model.mto like ?";
			objs.add('%' + mto + '%');
		}
		if (hasatt != null) {
			queryString += "and model.hasatt = ?";
			objs.add(hasatt);
		}
		
		if (startdate != null && enddate != null) {
			queryString += "and model.sentDate between ? and ?";
			objs.add(startdate);
			objs.add(enddate);
		}
		if (startdate != null && enddate == null) {
			queryString += "and model.sentDate > ?";
			objs.add(startdate);
		}
		if (startdate == null && enddate != null) {
			queryString += "and model.sentDate < ?";
			objs.add(enddate);
		}
		return normalDAO.getCountBySql(queryString, objs.toArray());
	}

	public Object setAllMailMessagesSeen(MailAccount ma,MailFolders mf)
	{
		String jpa = "UPDATE MailMessages AS model "+"SET model.isseen = ?"+"where model.account=? and model.folder = ?";
		Object obj = normalDAO.excute(jpa, Boolean.TRUE,ma,mf);
		return obj;
	}
	
	public List<MailAccountSign> findAllsign(MailAccount ma) {
		List<MailAccountSign> lists = normalDAO.findByProperty(
				MailAccountSign.class, "account", ma);
		return lists;
	}

	public MailAccountSign findsign(Long id) {
		MailAccountSign ms = (MailAccountSign)normalDAO.find(MailAccountSign.class, id);
		return ms;
	}
	public void createMailAccountSign(MailAccountSign sign) {
		normalDAO.save(sign);
	}

	public void updateMailAccountSign(MailAccountSign sign) {
		normalDAO.update(sign);
	}

	public MailAccount createDefaultMailAccountByUser(Users user,
			String inuser, String inpassword) {
		MailAccount ma = null;
		String email = user.getRealEmail();
		// 如果存在的账户，需要修改用户名和密码的
		if (email == null || email.indexOf('@') == -1) {
			return null;
		}
		List<MailAccount> lists = this.findALLbyUser(user, email);
		if (lists.size() > 0) {
			MailAccount oldma = finddefaultAccount(user);
			ma =  lists.get(0);
			if(!ma.getIsdefault())
			{
				ma.setIsdefault(Boolean.TRUE);
				if(inuser != null)
				{
					ma.setInuser(inuser);
				}
				if(inpassword != null)
				{
					ma.setInpassword(inpassword);
				}
				this.updateMailAccount(ma);
			}
			if (oldma.getId().longValue() != ma.getId().longValue()) {
				oldma.setIsdefault(Boolean.FALSE);
				this.updateMailAccount(oldma);
			}
			

		} else {
			ma = new MailAccount();
			ma.setUser(user);
			String domain = null;
			if (domain == null && email != null) {
				domain = email.substring(email.indexOf('@') + 1);
			}
			CommonMailConfig cmc = CommonMailConfig
					.createCommonMailConfig(domain);
			ma.setEmail(email);
			if (cmc != null) {

				// ma.setInpassword(inpassword);//没有用户名

				ma.setPersonName(email.substring(0, email.indexOf('@')));
				ma.setReplyToemail(null);

				ma.setIncomingServer(cmc.getIncomingServer());
				ma.setIncomingport(cmc.getIncomingport());
				ma.setInSSL(cmc.getInSSL());
				ma.setIncomingServerType(MailAccount.convertServerType(cmc
						.getIncomingServerType()));
				// ma.setInuser(inuser);//没有密码

				ma.setOutgoingServer(cmc.getOutgoingServer());
				ma.setOutgoingport(cmc.getOutgoingport());
				ma.setOutSSL(cmc.getOutSSL());
				ma.setOutgoingServerType(MailAccount.ServerType.SMTP);

				ma.setSmtpAuth(cmc.getSmtpAuth());
				ma.setSmtpAuthSameasin(cmc.getSmtpAuthSameasin());
				ma.setOutsmtpUser(null);
				ma.setOutsmtpPassword(null);
				ma.setDeleteafterdays(-1);
			}
			ma.setIsdefault(Boolean.TRUE);
			ma.setIsinnerAccount(Boolean.TRUE);
			ma.setInuser(inuser);
			ma.setInpassword(inpassword);
			ma.setPersonName(user.getRealName());
			// 如果有默认，把原先的默认设置修改为非默认
			MailAccount oldma = finddefaultAccount(user);
			if (oldma != null) {
				oldma.setIsdefault(Boolean.FALSE);
				this.updateMailAccount(oldma);
			}
			oldma = findinnerAccount(user);
			if (oldma != null) {
				oldma.setIsinnerAccount(Boolean.FALSE);
				this.updateMailAccount(oldma);
			}

			this.createMailAccount(ma);
		}
		return ma;
	}
	
	public void setDefaultMailAccount(long id,Users user)
	{
		List<MailAccount> lists = findALLbyUser(user);//将原先默认设置成非默认
		for (MailAccount mailAccount : lists) {
			if (mailAccount.getIsdefault()) {
				mailAccount.setIsdefault(Boolean.FALSE);
				updateMailAccount(mailAccount);
			}
		}
		MailAccount ma = findMailAccountById(id);
		if(ma == null)
		{
			return;
		}
		ma.setIsdefault(true);
		updateMailAccount(ma);
		// 修改users表
		UserService userservice = (UserService) ApplicationContext
				.getInstance().getBean(UserService.NAME);
		user = userservice.getUser(user.getId());
		user.setRealEmail(ma.getEmail());
		userservice.updataUserinfo(user);
	}
}
