import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { branchConsistency } from '../branchConsistency';
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { microService } from '../microService';

@Component({
  selector: 'app-consistency-across-branch-dialogue',
  templateUrl: './consistency-across-branch-dialogue.component.html',
  styleUrls: ['./consistency-across-branch-dialogue.component.css']
})
export class ConsistencyAcrossBranchDialogueComponent implements OnInit {

  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  branchCons!: branchConsistency;
  mservices: microService[] = [];
  services: string[] = [];
  service: string = "";
  nservices: string[] = [];
  recipients: string[] = [];
  branchList: string[] = [];
  checked = false;

  setFunction(service: string){
    this.service = service;
    for(let i=0;i<this.mservices.length;i++){
      if(this.mservices[i].service===service){
        this.branchList = this.mservices[i].branches.map((x:any) => x.name);
        break;
      }
    }
  }

  setBranch1(branchValue: any){
    this.branchCons.fromBranch = branchValue;
  }

  setBranch2(branchValue: any){
    this.branchCons.toBranch = branchValue;
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our fruit
    if (value) {
      this.recipients.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(recipient: string): void {
    const index = this.recipients.indexOf(recipient);

    if (index >= 0) {
      this.recipients.splice(index, 1);
    }
  }

  constructor(private dialogRef: MatDialogRef<ConsistencyAcrossBranchDialogueComponent>,@Inject(MAT_DIALOG_DATA) private data: branchConsistency, private _dataManagerService: DataManagerService) {
    this.branchCons = new branchConsistency();
  }

  checkConsistency(){
    if(this.service.length>0) this.nservices.push(this.service);
    this.branchCons.services = this.nservices;
    if(this.checked) this.branchCons.propertyValueEqual = true;
    else this.branchCons.propertyValueEqual = false;
    this.branchCons.email = {sendEmail: true, recipients: this.recipients};

    this._dataManagerService.sendBranchConsistencyEmail(this.branchCons).subscribe(data=>{
      this.dialogRef.close(this.branchCons);
    });
  }

  ngOnInit(): void {
    this._dataManagerService.getServicesList().subscribe(data => {
      this.mservices = JSON.parse(JSON.stringify(data));
      for(let i=0;i<this.mservices.length;i++){
        this.services.push(this.mservices[i].service);
      }
    });
  }

}
