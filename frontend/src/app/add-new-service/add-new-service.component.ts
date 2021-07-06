import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { microService } from '../microService';
import { DataManagerService } from '../shared/shared-services/data-manager.service';

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

  //profile module chip fucntions
  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      let flag = true;
      for(let i=0;i<this.profiles.length;i++){
        if(this.profiles[i].name===value){
          flag = false;
          break;
        }
      }
      if(flag) this.profiles.push({name: value});
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(profile: Profile): void {
    const index = this.profiles.indexOf(profile);

    if (index >= 0) {
      this.profiles.splice(index, 1);
    }
  }
  //profile module chip functions

  //branch module chip fucntions
  addBranch(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      let flag = true;
      for(let i=0;i<this.branches.length;i++){
        if(this.branches[i].name===value){
          flag = false;
          break;
        }
      }
      if(flag) this.branches.push({name: value, priority: 0});
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  removeBranch(branch: Branch): void {
    const index = this.branches.indexOf(branch);

    if (index >= 0) {
      this.branches.splice(index, 1);
    }
  }
  //branch module chip functions
  
  constructor(private dialogRef: MatDialogRef<AddNewServiceComponent>,@Inject(MAT_DIALOG_DATA) private data: microService, private _dataManagerService: DataManagerService) {
    this.addService=new microService();
    console.log(data);
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
        //this.dialogRef.close(this.addService);
      }
    }else{
      this.addService.gitRepository={url: this.url, username: this.username, password: this.password};
      if(this.isServiceValid && this.isDirValid && this.isUrlValid && this.isUsernameValid && this.isPasswordValid && this.isProfileValid && this.isBranchValid){
        this.executeAddService();
        //this.dialogRef.close(this.addService);
      }
    }
    
  }

  executeAddService(){
    this._dataManagerService.addService(this.addService).subscribe(data=>{
      //console.log(data);
      this.dialogRef.close(this.addService);
    });
  }

  ngOnInit(): void {
  }

}
