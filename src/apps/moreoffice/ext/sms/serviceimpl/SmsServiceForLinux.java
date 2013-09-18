package apps.moreoffice.ext.sms.serviceimpl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.smslib.Message.MessageEncodings;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

import apps.moreoffice.ext.sms.model.SmsMessage;
import apps.moreoffice.ext.sms.service.SmsService;
import apps.moreoffice.ext.sms.utils.ConnInit;
import apps.moreoffice.ext.sms.utils.SmsException;

/** Linux下短信发送、接收类 */

public class SmsServiceForLinux implements SmsService
{
    /* 读取的文件名 */
    private String readUrl;
    /* 写入的文件名 */
    private String writeUrl;

    private SmsMessage smsMessage;

    public SmsServiceForLinux()
    {
    }

    public SmsServiceForLinux(SmsMessage smsMessage)
    {
        this.smsMessage = smsMessage;
    }

    public void run()
    {
        try
        {
            Properties properties = ConnInit.loadFile();
            String pattern = properties.getProperty("accessPattern");
            pattern = (pattern != null) ? pattern.trim() : "";

            if ("gsmModem".equals(pattern))
            {
//              saveSmsMessage(smsMessage); // 将消息持久化到消息队列
                 sendByGsmModem(smsMessage); //采用多线程发
            }
            else if ("httpInterface".equals(pattern))
            {
                sendByHttpBat(smsMessage, pattern);
            }
            else
            {
                System.out.println(" ***** 未使用短信功能！***** ");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将发消息任务持久化到消息队列库中
     * 
     * @param smsMessage2
     */
    private void saveSmsMessage(SmsMessage smsMessage)
    {
        List<String> receivers = smsMessage.getReceivers();
        int count = receivers.size();
        String sender = smsMessage.getSender();
        String content = smsMessage.getContent();

        for (int i = 0; i < count; i++)
        {
            saveSmsMessage(sender, receivers.get(i), content);
        }
    }

    /**
     * 将一条消息持久化到消息队列库中
     * 
     * @param smsMessage
     */
    private void saveSmsMessage(String sender, String receiver, String content)
    {
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = ConnInit.getInstance().getConnection();

            String sql = "insert smsMessage(sender, receiver, createTime, content) values(?, ?, ?, ?)";
            PreparedStatement prStm = conn.prepareStatement(sql);
            prStm.setString(1, sender);
            prStm.setString(2, receiver);
            prStm.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            prStm.setString(4, content);

            prStm.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            ConnInit.getInstance().free(ps, conn);
        }
    }

    /**
     * 发送短信
     * 
     * @param smsMessage
     *            短信消息
     * @throws SmsException
     */
    public void sendSms(SmsMessage smsMessage) throws SmsException
    {

    }

    /**
     * 第三方批量发短信
     * 
     * @param smsMessage
     * @param accessPattern
     */
    private void sendByHttpBat(SmsMessage smsMessage, String accessPattern)
    {
        try
        {
            List<String> receivers = smsMessage.getReceivers();

            for (int i = 0; i < receivers.size(); i++)
            {
                String receiver = receivers.get(i);
                if (receiver != null)
                {
                    sendByHttp(receiver, smsMessage.getContent(), accessPattern);
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 通过GSM Modem 进行发送短信
     * 
     * @param receiver
     * @param content
     */
    private synchronized void sendByGsmModem(SmsMessage smsMessage)
    {
        System.out.println("portName: " + ConnInit.portName + "   portRate: " + ConnInit.portRate);
        SerialModemGateway gateway = new SerialModemGateway("*", ConnInit.portName,
                ConnInit.portRate, "Siemens", "T35"); // 9600 8N1        
        try
        {
            gateway.setOutbound(true);

            Service.getInstance().addGateway(gateway);
            Service.getInstance().startService();
            // printModemInfo("gsmModem", gateway);

            List<String> receivers = smsMessage.getReceivers();

            for (int i = 0; i < receivers.size(); i++)
            {
                String receiver = receivers.get(i);
                if (receiver != null)
                {
                    OutboundMessage msg = new OutboundMessage(receiver,
                            smsMessage.getContent());// Send a message
                                                        // synchronously.
                    msg.setEncoding(MessageEncodings.ENCUCS2);

                    boolean status = Service.getInstance().sendMessage(msg);

                    System.out.println("** msg send"
                            + (status == true ? "sucessfull" : "failed"));
                    System.out.println(msg);
                }
            }
        }
        catch (Throwable e)
        {
            System.out.println("短信发送异常，请检查短信猫配置！");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                Service.getInstance().stopService();
                Service.getInstance().removeGateway(gateway);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印GSM Modem 信息
     * 
     * @param type
     * @param gateway
     */
    private void printModemInfo(String type, SerialModemGateway gateway)
    {
        if ("gsmModem".equals(type))
        { // 目前是gsmModem
            try
            {
                System.out.println();
                System.out.println("  Modem Inform ation:");
                System.out.println("  Manufacturer: "
                        + gateway.getManufacturer());
                System.out.println("  Model: " + gateway.getModel());
                System.out.println("  Serial No: " + gateway.getSerialNo());
                System.out.println("  SIM IMSI: " + gateway.getImsi());
                System.out.println("  Signal Level: "
                        + gateway.getSignalLevel() + " dBm");
                System.out.println("  Battery Level: "
                        + gateway.getBatteryLevel() + "%");
                System.out.println();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过通讯服务商来提供的 http 接口进行短信发送
     * 
     * @param receiver
     * @param content
     * @param accessPattern
     */
    private void sendByHttp(String receiver, String content,
            String accessPattern)
    {
        try
        {
            URL readSource = new URL(accessPattern.substring(0, accessPattern
                    .indexOf("?")));
            URLConnection urlConn = readSource.openConnection();
            HttpURLConnection httpUrlConn = (HttpURLConnection) urlConn;

            httpUrlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            httpUrlConn.setUseCaches(false);
            httpUrlConn.setRequestMethod("POST");

            httpUrlConn.connect();

            String[] params = accessPattern.substring(
                    accessPattern.indexOf("?") + 1).split("&");
            StringBuffer buffer = setParams(params, receiver, content);
            OutputStream os = httpUrlConn.getOutputStream();
            os.write(buffer.toString().getBytes("GB2312"));
            os.close();

            // print
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    httpUrlConn.getInputStream()));
            String str = br.readLine();
            while (str != null)
            {
                System.out.println(new String(str.getBytes(), "utf-8"));
                str = br.readLine();
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 为请求设置参数
     * 
     * @param params
     *            授权口令、密码及发送格式
     * @param receiver
     *            接收号码
     * @param content
     *            发送内容
     * @return 待发送参数
     */
    public StringBuffer setParams(String[] params, String receiver,
            String content)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(params[0]).append("&").append(params[1]);// permission

        buffer.append("&").append(params[params.length - 2] + receiver);
        buffer.append("&").append(params[params.length - 1] + content);
        return buffer;
    }

    /**
     * 读取短信
     * 
     * @return 接收到的短信
     * @throws SmsException
     */
    public SmsMessage receiveSms() throws SmsException
    {
        return null;
    }

    public String getReadUrl()
    {
        return readUrl;
    }

    public void setReadUrl(String readUrl)
    {
        this.readUrl = readUrl;
    }

    public String getWriteUrl()
    {
        return writeUrl;
    }

    public void setWriteUrl(String writeUrl)
    {
        this.writeUrl = writeUrl;
    }

    public SmsMessage getSmsMessage()
    {
        return smsMessage;
    }

    public void setSmsMessage(SmsMessage smsMessage)
    {
        this.smsMessage = smsMessage;
    }

}
