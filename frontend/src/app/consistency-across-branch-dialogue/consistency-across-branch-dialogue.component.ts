import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { branchConsistency } from '../branchConsistency';
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { microService } from '../microService';
import { MatSnackBar } from '@angular/material/snack-bar';

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

  visibleProgressSpinner = false;
  isServiceValid = true;
  isBranch1Valid = true;
  isBranch2Valid = true;
  isRecipientValid = true;
  isEmailValid = true;

  constructor(private dialogRef: MatDialogRef<ConsistencyAcrossBranchDialogueComponent>,
    @Inject(MAT_DIALOG_DATA) private data: branchConsistency, private _dataManagerService: DataManagerService,
    private _snackBar: MatSnackBar) {
    this.branchCons = new branchConsistency();
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this._dataManagerService.getServicesList().subscribe(data => {
      this.mservices = JSON.parse(JSON.stringify(data));
      for(let i=0;i<this.mservices.length;i++){
        this.services.push(this.mservices[i].service);
      }
    });
  }

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

    if (value) {
      let flag = true;
      for(let i=0;i<this.recipients.length;i++){
        if(this.recipients[i]===value){
          flag = false;
          break;
        }
      }
      if(flag){
        if(this.validateEmail(value)){
          this.isEmailValid = true;
          this.recipients.push(value);
        }else{
          this.isEmailValid = false;
        }
      }
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

  validateEmail(email: string) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }

  checkConsistency(){
    this.isServiceValid = this.service.length>0;
    this.isBranch1Valid = this.branchCons.fromBranch.length>0;
    this.isBranch2Valid = this.branchCons.toBranch.length>0;
    this.isRecipientValid = this.recipients.length>0;
    if(this.isServiceValid && this.isBranch1Valid && this.isBranch2Valid && this.isRecipientValid){
      this.visibleProgressSpinner = true;
      this.nservices.push(this.service);
      this.branchCons.services = this.nservices;
      if(this.checked) this.branchCons.propertyValueEqual = true;
      else this.branchCons.propertyValueEqual = false;
      this.branchCons.email = {sendEmail: true, recipients: this.recipients};

      this._dataManagerService.sendBranchConsistencyEmail(this.branchCons).subscribe(data=>{
        this.visibleProgressSpinner = false;
        let Msg = "Mail Sent Successfully";
        let simpleSnackBarRef = this._snackBar.open(Msg,"Close");
        setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 5000);
        this.dialogRef.close(this.branchCons);
      },
      
      err=>{
        this.visibleProgressSpinner = false;
        let errorMsg = (err.error.error.errorMessage);
        let simpleSnackBarRef = this._snackBar.open(errorMsg,"Close");
        setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 100000);
      }
      );
    }
  }
  closeDialog(){
    this.dialogRef.close(ConsistencyAcrossBranchDialogueComponent);
  }

}
