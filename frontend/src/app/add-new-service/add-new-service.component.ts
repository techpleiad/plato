import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { microService } from '../microService';
import { DataManagerService } from '../shared/shared-services/data-manager.service';
import { MatSnackBar } from '@angular/material/snack-bar';

export interface Profile {
  name: string;
}

export interface Branch {
  name: string;
  priority: number;
}

@Component({
  selector: 'app-add-new-service',
  templateUrl: './add-new-service.component.html',
  styleUrls: ['./add-new-service.component.css']
})
export class AddNewServiceComponent implements OnInit {
  //profile variables
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  profiles: Profile[] = [];
  //profile variables

  //branch variables
  branches: Branch[] = [];
  //branch variables

  addService!: microService;
  isServiceValid = true;
  isDirValid = true;
  isUrlValid = true;
  isUsernameValid = true;
  isPasswordValid = true;
  isProfileValid = true;
  isBranchValid = true;
  checked = false;
  url: string="";
  username: string="";
  password: string="";

  constructor(private dialogRef: MatDialogRef<AddNewServiceComponent>,@Inject(MAT_DIALOG_DATA) private data: microService,
  private _dataManagerService: DataManagerService,  private _snackBar: MatSnackBar) {
    this.addService=new microService();
    this.dialogRef.disableClose = true;
  }

  ngOnInit(): void {
  }
  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      this.profiles.push({name: value});
    }
    event.chipInput!.clear();
  }

  remove(profile: Profile): void {
    const index = this.profiles.indexOf(profile);
    if (index >= 0) {
      this.profiles.splice(index, 1);
    }
  }
  addBranch(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      this.branches.push({name: value, priority: 0});
    }
    event.chipInput!.clear();
  }

  removeBranch(branch: Branch): void {
    const index = this.branches.indexOf(branch);
    if (index >= 0) {
      this.branches.splice(index, 1);
    }
  }
  addNewService(){
    for(let i=0;i<this.branches.length;i++){
      this.branches[i].priority=i+1;
    }
    this.addService.branches=this.branches;
    this.addService.profiles=this.profiles;
    this.isServiceValid=this.addService.service.length>0;
    this.isDirValid=this.addService.directory.length>0;
    this.isUrlValid=this.url.length>0;
    this.isUsernameValid=this.username.length>0;
    this.isPasswordValid=this.password.length>0;
    this.isProfileValid=this.profiles.length>0;
    this.isBranchValid=this.branches.length>0;
    if(this.checked){
      this.addService.gitRepository={url: this.url};
      if(this.isServiceValid && this.isDirValid && this.isUrlValid && this.isProfileValid && this.isBranchValid){
        this.executeAddService();
      }
    }else{
      this.addService.gitRepository={url: this.url, username: this.username, password: this.password};
      if(this.isServiceValid && this.isDirValid && this.isUrlValid && this.isUsernameValid && this.isPasswordValid && this.isProfileValid && this.isBranchValid){
        this.executeAddService();
      }
    }
    
  }
  executeAddService(){
    this._dataManagerService.addService(this.addService).subscribe(data=>{
      //console.log(data);
      this.dialogRef.close(this.addService);
    },
    err=>{
      let errorMsg = (err.error.error.errorMessage);
      let simpleSnackBarRef = this._snackBar.open(errorMsg,"Close");
      setTimeout(simpleSnackBarRef.dismiss.bind(simpleSnackBarRef), 100000);
    }
    );
  }
  closeDialog(){
    this.dialogRef.close(AddNewServiceComponent);
  }


}
