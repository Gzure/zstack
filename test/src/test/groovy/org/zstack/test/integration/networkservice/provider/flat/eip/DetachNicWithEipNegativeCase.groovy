package org.zstack.test.integration.networkservice.provider.flat.eip

import org.zstack.header.network.service.NetworkServiceType
import org.zstack.kvm.KVMAgentCommands
import org.zstack.kvm.KVMConstant
import org.zstack.network.service.eip.EipConstant
import org.zstack.network.service.flat.FlatNetworkServiceConstant
import org.zstack.network.service.userdata.UserdataConstant
import org.zstack.sdk.DetachL3NetworkFromVmAction
import org.zstack.sdk.EipInventory
import org.zstack.sdk.L3NetworkInventory
import org.zstack.sdk.VipInventory
import org.zstack.sdk.VmInstanceInventory
import org.zstack.test.integration.networkservice.provider.NetworkServiceProviderTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase
import org.zstack.utils.data.SizeUnit

class DetachNicWithEipNegativeCase extends SubCase {

    EnvSpec env

    @Override
    void setup() {
        useSpring(NetworkServiceProviderTest.springSpec)
    }

    @Override
    void environment() {
        env = env {
            instanceOffering {
                name = "instanceOffering"
                memory = SizeUnit.GIGABYTE.toByte(8)
                cpu = 4
            }

            sftpBackupStorage {
                name = "sftp"
                url = "/sftp"
                username = "root"
                password = "password"
                hostname = "localhost"

                image {
                    name = "image"
                    url = "http://zstack.org/download/test.qcow2"
                }
            }

            zone {
                name = "zone"
                description = "test"

                cluster {
                    name = "cluster"
                    hypervisorType = "KVM"

                    kvm {
                        name = "kvm"
                        managementIp = "localhost"
                        username = "root"
                        password = "password"
                    }

                    attachPrimaryStorage("local")
                    attachL2Network("l2")
                }

                localPrimaryStorage {
                    name = "local"
                    url = "/local_ps"
                }

                l2NoVlanNetwork {
                    name = "l2"
                    physicalInterface = "eth0"

                    l3Network {
                        name = "l3"

                        service {
                            provider = FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE_STRING
                            types = [NetworkServiceType.DHCP.toString(), UserdataConstant.USERDATA_TYPE_STRING,
                                     EipConstant.EIP_NETWORK_SERVICE_TYPE]
                        }

                        ip {
                            startIp = "10.168.100.10"
                            endIp = "10.168.100.100"
                            netmask = "255.255.255.0"
                            gateway = "10.168.100.1"
                        }
                    }

                    l3Network {
                        name = "pubL3"

                        ip {
                            startIp = "11.168.100.10"
                            endIp = "11.168.100.100"
                            netmask = "255.255.255.0"
                            gateway = "11.168.100.1"
                        }
                    }
                }

                attachBackupStorage("sftp")
            }

            vm {
                name = "vm"
                useImage("image")
                useL3Networks("l3")
                useInstanceOffering("instanceOffering")
            }
        }
    }

    @Override
    void test() {
        env.create {
            testDetachNicWithEipNegative()
        }
    }

    void testDetachNicWithEipNegative() {
        VmInstanceInventory vm = env.inventoryByName("vm")
        def pubL3 = env.inventoryByName("pubL3") as L3NetworkInventory
        def vip = createVip {
            name = "vip"
            l3NetworkUuid = pubL3.uuid
        } as VipInventory
        def eip = createEip {
            name = "eip"
            vipUuid = vip.uuid
            vmNicUuid = vm.getVmNics().get(0).uuid
        } as EipInventory

        env.simulator(KVMConstant.KVM_DETACH_NIC_PATH) {
            def rsp = new KVMAgentCommands.DetachNicRsp()
            rsp.setError("on purpose")
            return rsp
        }

        DetachL3NetworkFromVmAction action = new DetachL3NetworkFromVmAction()
        action.sessionId = adminSession()
        action.vmNicUuid = vm.getVmNics().get(0).uuid
        DetachL3NetworkFromVmAction.Result res = action.call()
        assert res.error != null
    }

    @Override
    void clean() {
        env.delete()
    }
}
