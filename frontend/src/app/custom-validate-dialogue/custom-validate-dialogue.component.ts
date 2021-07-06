import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { customValidate } from '../customValidate';
import { microService } from '../microService';
import { DataManagerService } from '../shared/shared-services/data-manager.service';

@Component({
  selector: 'app-custom-validate-dialogue',
  templateUrl: './custom-validate-dialogue.component.html',
  styleUrls: ['./custom-validate-dialogue.component.css']
})
export class CustomValidateDialogueComponent implements OnInit {

  branches = new FormControl();
  profiles = new FormControl();

  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  cusVal!: customValidate;
  mservices: microService[] = [];
  services: string[] = [];
  recipients: string[] = [];
  branchList: string[] = [];
  profileList: string[] = [];

  visibleProgressSpinner = false;
  isServiceValid = true;
  isBranchValid = true;
  isProfileValid = true;
  isRecipientValid = true;

  constructor(private dialogRef: MatDialogRef<CustomValidateDialogueComponent>,
    @Inject(MAT_DIALOG_DATA) private data: customValidate, private _dataManagerService: DataManagerService,
    private _snackBar: MatSnackBar) {
    this.cusVal = new customValidate();
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

  setFunction(service: string){
    this.cusVal.services = [];
    this.cusVal.services.push(service);
    for(let i=0;i<this.mservices.length;i++){
      if(this.mservices[i].service===service){
        this.branchList = this.mservices[i].branches.map((x:any) => x.name);
        this.profileList = this.mservices[i].profiles.map((x:any) => x.name);
        break;
      }
    }
  }


  customValidate(){
    this.cusVal.branches = this.branches.value;
    this.cusVal.profiles = this.profiles.value;
    this.cusVal.email = {sendEmail: true, recipients: this.recipients};

    this.isServiceValid = this.cusVal.services.length>0;
    if(this.branches.value!=null) this.isBranchValid = this.branches.value.length>0;
    else this.isBranchValid = false;
    if(this.profiles.value!=null) this.isProfileValid = this.profiles.value.length>0;
    else this.isProfileValid = false;
    this.isRecipientValid = this.recipients.length>0;
    if(this.isServiceValid && this.isBranchValid && this.isProfileValid && this.isRecipientValid){
      this.visibleProgressSpinner = true;
      this.cusVal.branches = this.branches.value;
      this.cusVal.profiles = this.profiles.value;
      this.cusVal.email = {sendEmail: true, recipients: this.recipients};

      this._dataManagerService.sendCustomValidateEmail(this.cusVal).subscribe(data=>{
        this.visibleProgressSpinner = false;
        this.dialogRef.close(this.cusVal);
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
    this.dialogRef.close(CustomValidateDialogueComponent);
  }

}
