import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { microService } from '../microService';
import { ConfigFilesService } from '../shared/shared-services/config-files.service';

@Component({
  selector: 'app-workspace-dialogue',
  templateUrl: './workspace-dialogue.component.html',
  styleUrls: ['./workspace-dialogue.component.css']
})
export class WorkspaceDialogueComponent implements OnInit {

  mservice:microService;

  profileList: string[] = [];
  branchList: string[] = [];
  functionList: string[] = [];
  
  functionValue: any;
  branchValue: any;
  profileValue: any;

  isBranchReq = false;
  isProfileReq = false;
  canProfileDefault = false;

  displayData: any;

  visibleProgressSpinner = false;

  constructor(@Inject(MAT_DIALOG_DATA) public data: microService, private _configFiles: ConfigFilesService) {
    this.functionList = ["merged","individual"];
    this.mservice = data;
    this.branchValue = "";
    this.profileValue = "";

    this.profileList = this.mservice.profiles.map((x: any) => x.name);
    this.branchList = this.mservice.branches.map((x:any) => x.name);
    

   }

  ngOnInit(): void {
  }
  setFunction(functionValue: any){
    this.functionValue = functionValue;
    this.setBranchProfileReq();
  }
  setBranchProfileReq(){
    if(this.functionValue==="merged" || this.functionValue==="individual"){
      this.isBranchReq = true;
      this.isProfileReq = true;
    }
    if(this.functionValue==="individual"){
      this.canProfileDefault = true;
    }
    else{
      this.canProfileDefault = false;
    }
    
  }
  setBranch(branchValue: any){
    this.branchValue = branchValue;
    console.log("Branch is set to ",this.branchValue);
  }
  setProfile(profileValue: any){
    this.profileValue = profileValue;
    console.log("Profile is set to ",this.profileValue);
  }
  

  sendToContentDisplay(){
    // Progress Spinner 
    this.visibleProgressSpinner = true;

    this._configFiles.getFile(this.mservice.service,this.functionValue, this.branchValue,this.profileValue)
    .subscribe(data => {
      if(data){
        this.visibleProgressSpinner = false;
      }
      
      this.displayData = data;
      //console.log(this.displayData);
    });

  }

  

}
