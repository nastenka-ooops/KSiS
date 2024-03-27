import java.io.IOException;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
public class ResourcesSearcher {
    public void getResources(Winnetwk.NETRESOURCE.ByReference NetResourceByReference) {

        WinNT.HANDLEByReference handleByReference = new WinNT.HANDLEByReference();

        Mpr.INSTANCE.WNetOpenEnum(Winnetwk.RESOURCESCOPE.RESOURCE_GLOBALNET,
                Winnetwk.RESOURCETYPE.RESOURCETYPE_ANY,
                Winnetwk.RESOURCEUSAGE.RESOURCEUSAGE_ALL,
                NetResourceByReference, handleByReference);

        WinNT.HANDLE handle = handleByReference.getValue();

        Pointer lpNetResource = new Memory(2048);

        IntByReference count = new IntByReference();
        count.setValue(-1);
        IntByReference buffer = new IntByReference();
        buffer.setValue(1000);

        int result = Mpr.INSTANCE.WNetEnumResource(handle, count, lpNetResource, buffer);

        if (result == 0) {
            Winnetwk.NETRESOURCE netResource = new Winnetwk.NETRESOURCE(lpNetResource); // Инициализируем структуру NETRESOURCE
            Winnetwk.NETRESOURCE[] netResources = (Winnetwk.NETRESOURCE[]) netResource.toArray(count.getValue()); // Преобразуем буфер в массив структур
            for (int i = 0; i < count.getValue(); i++) {
                System.out.println("dwScope: " + netResources[i].dwScope);
                System.out.println("dwType: " + netResources[i].dwType);
                System.out.println("dwDisplayType: " + netResources[i].dwDisplayType);
                System.out.println("dwUsage: " + netResources[i].dwUsage);
                System.out.println("lpLocalName: " + netResources[i].lpLocalName);
                System.out.println("lpRemoteName: " + netResources[i].lpRemoteName);
                System.out.println("lpComment: " + netResources[i].lpComment);
                System.out.println("lpProvider: " + netResources[i].lpProvider);
                System.out.println();
                if ((netResources[i].dwUsage & Winnetwk.RESOURCEUSAGE.RESOURCEUSAGE_CONTAINER) == Winnetwk.RESOURCEUSAGE.RESOURCEUSAGE_CONTAINER) {
                    getResources(new Winnetwk.NETRESOURCE.ByReference(netResources[i].getPointer()));
                }
                }
                Mpr.INSTANCE.WNetCloseEnum(handle);
            }
        }
    }

