import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { microService } from '../microService';
import { profileConsistency } from '../profileConsistency';
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { MatSnackBar } from '@angular/material/snack-bar';

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
  isEmailValid = true;

  constructor(private dialogRef: MatDialogRef<ConsistencyAcrossProfileDialogueComponent>,
    @Inject(MAT_DIALOG_DATA) private data: profileConsistency, private _dataManagerService: DataManagerService,
    private _snackBar: MatSnackBar) {
    this.profileCons = new profileConsistency();
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

  setBranch(branchValue: any){
    this.branchValue = branchValue;
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
        let Msg = "Mail Sent Successfully";
        let simpleSnackBarRef = this._snackBar.open(Msg,"Close");
        setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 5000);
        this.dialogRef.close(this.profileCons);
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
    this.dialogRef.close(ConsistencyAcrossProfileDialogueComponent);
  }


}
