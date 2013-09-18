package apps.transmanager.weboffice.domain;

/* 由于GWT和Java序列化方式的不同，采用该接口做适配器，使得在把domain对象应用到不同的场景的时候,
 * 不需要修改所有的Domain对象，仅仅修改该适配器类即可。
 */

public interface SerializableAdapter extends com.google.gwt.user.client.rpc.IsSerializable, java.io.Serializable
{

}
