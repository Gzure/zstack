package org.zstack.storage.ceph.primary;

import org.zstack.header.core.ApiTimeout;
import org.zstack.header.image.APICreateDataVolumeTemplateFromVolumeMsg;
import org.zstack.header.image.APICreateRootVolumeTemplateFromRootVolumeMsg;
import org.zstack.header.message.NeedReplyMessage;
import org.zstack.header.storage.primary.PrimaryStorageMessage;
import org.zstack.header.volume.APICreateDataVolumeFromVolumeSnapshotMsg;

/**
 * Created by xing5 on 2016/4/29.
 */
@ApiTimeout(apiClasses = {APICreateDataVolumeTemplateFromVolumeMsg.class, APICreateRootVolumeTemplateFromRootVolumeMsg.class,
        APICreateDataVolumeFromVolumeSnapshotMsg.class})
public class UploadBitsToBackupStorageMsg extends NeedReplyMessage implements PrimaryStorageMessage {
    private String primaryStorageUuid;
    private String primaryStorageInstallPath;
    private String backupStorageUuid;
    private String backupStorageInstallPath;
    private String imageUuid;

    @Override
    public String getPrimaryStorageUuid() {
        return primaryStorageUuid;
    }

    public void setPrimaryStorageUuid(String primaryStorageUuid) {
        this.primaryStorageUuid = primaryStorageUuid;
    }

    public String getPrimaryStorageInstallPath() {
        return primaryStorageInstallPath;
    }

    public void setPrimaryStorageInstallPath(String primaryStorageInstallPath) {
        this.primaryStorageInstallPath = primaryStorageInstallPath;
    }

    public String getBackupStorageUuid() {
        return backupStorageUuid;
    }

    public void setBackupStorageUuid(String backupStorageUuid) {
        this.backupStorageUuid = backupStorageUuid;
    }

    public String getBackupStorageInstallPath() {
        return backupStorageInstallPath;
    }

    public void setBackupStorageInstallPath(String backupStorageInstallPath) {
        this.backupStorageInstallPath = backupStorageInstallPath;
    }

    public String getImageUuid() {
        return imageUuid;
    }

    public void setImageUuid(String imageUuid) {
        this.imageUuid = imageUuid;
    }
}
