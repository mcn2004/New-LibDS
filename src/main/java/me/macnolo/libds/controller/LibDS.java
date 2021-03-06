/*
 * ----------------------------------------------------------------------------
 *  Copyright (c) Manuel Diaz Rojo form WinT 3794. All Rights Reserved.
 *  Open Source Software - may be modified and shared by FRC teams.
 *  This code is under MIT License. Check LICENSE file at project root .
 * ----------------------------------------------------------------------------
 */

package me.macnolo.libds.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;

import me.macnolo.libds.enums.Alliance;
import me.macnolo.libds.enums.IpFormats;
import me.macnolo.libds.enums.Mode;
import me.macnolo.libds.enums.PackageTypes;
import me.macnolo.libds.enums.Protocol;
import me.macnolo.libds.etc.IpFormater;
import me.macnolo.libds.etc.Utilities;

public class LibDS {
    private int team;
    private Alliance alliance;
    private Mode mode;
    private Protocol protocol;

    private byte[] robotIp;
    private byte[] fmsIp;
    private byte[] radioIp;

    private InetAddress robotAddress;
    private InetAddress fmsAddress;
    private InetAddress radioAddress;

    private IpFormats ipTypeSelected;

    private Controller controller;

    public LibDS(int team, Alliance alliance, Mode mode, Protocol protocol, byte[] robotIp) {
        byte[] tmp;
        this.team = team;
        this.alliance = alliance;
        this.mode = mode;
        this.protocol = protocol;
        this.robotIp = robotIp;

        switch(protocol) {
            case AERIAL_ASSIST:
                ipTypeSelected = IpFormats.IP_1;
                if(robotIp == null || robotIp.equals("")){
                    this.robotIp = IpFormater.getAddress(ipTypeSelected, team,(byte) 1);
                }
                this.fmsIp = IpFormater.getAddress(ipTypeSelected, team, (byte) 2);
                this.radioIp = IpFormater.getAddress(ipTypeSelected, team, (byte) 3);
                break;
        }

        try {
            if(this.robotIp != null) {
                switch (ipTypeSelected) {
                    case IP_1:
                        robotAddress = InetAddress.getByAddress(this.robotIp);
                        break;
                    default:
                        robotAddress = InetAddress.getByName(this.robotIp.toString());
                        break;
                }
            }

            if (this.fmsIp != null) {
                fmsAddress = InetAddress.getByAddress(this.fmsIp);
            }

            if (this.radioIp != null) {
                radioAddress = InetAddress.getByAddress(this.radioIp);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        controller = new Controller(this.team, this.alliance, this.mode, this.protocol);
        controller.start();

        controller.setRobotAddress(robotAddress);
        controller.setFMSAddress(fmsAddress);
        controller.setRadioAddress(radioAddress);

        tmp = controller.createPackage(PackageTypes.ROBOT);
        controller.sendPackage(PackageTypes.ROBOT, tmp);
    }

    public InterruptedException close(){
        try {
            controller.setIsRunning(false);
            controller.join();
        } catch (InterruptedException e) {
            return e;
        }
        return null;
    }

    public void setNewAlliance(Alliance alliance){
        byte[] tmp;
        controller.setAlliance(alliance);
        tmp = controller.createPackage(PackageTypes.ROBOT);
        controller.sendPackage(PackageTypes.ROBOT, tmp);
    }


    public void setNewMode(Mode mode){
        byte[] tmp;
        controller.setMode(mode);
    }

    public void setEnable(boolean enable) {
        if(enable) {

        }
    }

    public void updateConfig(){
        byte[] tmp;
        tmp = controller.createPackage(PackageTypes.ROBOT);
        controller.sendPackage(PackageTypes.ROBOT, tmp);
    }

    public byte[] getData(){
        byte[] data = new byte[Utilities.DATA_TRANSFER_LENGHT];
        /*
        data[0] = (byte) (controller.isRobotPackage() ? 1 : 0);
        data[1] = (byte) (controller.isFMSPackage() ? 1 : 0);
        data[2] = (byte) (controller.getRobotPackagesSent());
        data[3] = (byte) (controller.getRobotPackagesReceived());
*/
        return data;
    }
}