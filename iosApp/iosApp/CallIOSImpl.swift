//
//  CallIOSImpl.swift
//  iosApp
//
//  Created by แมนยูไน๋แตด on 18/3/2568 BE.
//  Copyright © 2568 BE orgName. All rights reserved.
//

import Foundation
import CoreBluetooth
import ComposeApp
import UIKit

class CallIOSImpl: NSObject, CallIOS {
    
    private var peripheralVC: PeripheralViewController?
    
    override init() {
            super.init()
            if let vc = UIApplication.shared.keyWindow?.rootViewController as? PeripheralViewController {
                self.peripheralVC = vc
            }
        }

        func startPeripheral() {
            DispatchQueue.main.async {
                self.peripheralVC?.advertisingSwitch.setOn(true, animated: true)
                self.peripheralVC?.peripheralManager?.startAdvertising(nil)
            }
        }

        func stopPeripheral() {
            DispatchQueue.main.async {
                self.peripheralVC?.advertisingSwitch.setOn(false, animated: true)
                self.peripheralVC?.peripheralManager?.stopAdvertising()
            }
        }

}
