import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { microService } from '../microService';
import { profileConsistency } from '../profileConsistency';
import { DataManagerService } from '../shared/shared-services/data-manager.service';

@Component({
  selector: 'app-consistency-across-profile-dialogue',
  templateUrl: './consistency-across-profile-dialogue.component.html',
  styleUrls: ['./consistency-across-profile-dialogue.component.css']
})
export class ConsistencyAcrossProfileDialogueComponent implements OnInit {

  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  profileCons!: profileConsistency;
  mservices: microService[] = [];
  services: string[] = [];
  service: string = "";
  nservices: string[] = [];
  recipients: string[] = [];
  branchList: string[] = [];
  branchValue: string = "";
  checked = false;

  visibleProgressSpinner = false;
  isServiceValid = true;
  isBranchValid = true;
  isRecipientValid = true;

  setFunction(service: string){
    this.service = service;
    for(let i=0;i<this.mservices.length;i++){
      if(this.mservices[i].service===service){
        this.branchList = this.mservices[i].branches.map((x:any) => x.name);
        break;
      }
    }
  }

  setBranch(branchValue: any){
    this.branchValue = branchValue;
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

  constructor(private dialogRef: MatDialogRef<ConsistencyAcrossProfileDialogueComponent>,@Inject(MAT_DIALOG_DATA) private data: profileConsistency, private _dataManagerService: DataManagerService) {
    this.profileCons = new profileConsistency();
  }

  checkConsistency(){
    this.isServiceValid = this.service.length>0;
    this.isBranchValid = this.branchValue.length>0;
    this.isRecipientValid = this.recipients.length>0;
    if(this.isServiceValid && this.isBranchValid && this.isRecipientValid){
      this.visibleProgressSpinner = true;
      this.nservices.push(this.service);
      this.profileCons.services = this.nservices;
      if(this.checked) this.profileCons.includeSuppressed = true;
      else this.profileCons.includeSuppressed = false;
      this.profileCons.email = {sendEmail: true, recipients: this.recipients};

      this._dataManagerService.sendProfileConsistencyEmail(this.profileCons, this.branchValue).subscribe(data=>{
        this.visibleProgressSpinner = false;
        this.dialogRef.close(this.profileCons);
      });
    }
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
