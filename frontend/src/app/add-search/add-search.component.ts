import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddNewServiceComponent } from '../add-new-service/add-new-service.component';
import { branchConsistency } from '../branchConsistency';
import { ConsistencyAcrossBranchDialogueComponent } from '../consistency-across-branch-dialogue/consistency-across-branch-dialogue.component';
import { microService } from '../microService';

@Component({
  selector: 'app-add-search',
  templateUrl: './add-search.component.html',
  styleUrls: ['./add-search.component.css']
})
export class AddSearchComponent implements OnInit {
  @Output() reload_display_services = new EventEmitter();
  @Output() filter_display_services = new EventEmitter();
  

  [x: string]: any;

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }
  openAddServiceDialog(){
    const dialogRef = this.dialog.open(AddNewServiceComponent,{
      //disableClose:true,
      width: "600px",
      minHeight: "300px"
    
    });

    dialogRef.afterClosed().subscribe((result: microService) => {
      if(result){
        //this._dataManagerService.addService(result);
        console.log(result);
        this.reload_display_services.emit();
      }
      else{
        console.log("null value");
      }
    });
  }

  filterServices(event: any){
    let searchText = event.target.value;
    //console.log(searchText)
    if(searchText===""){
      this.reload_display_services.emit();
    }
    else{
      this.filter_display_services.emit(searchText);
    }
  }

  consistencyAcrossBranch(){
    const dialogRef = this.dialog.open(ConsistencyAcrossBranchDialogueComponent,{
      width: "600px",
      minHeight: "300px"
    });

    dialogRef.afterClosed().subscribe((result: branchConsistency)=>{
      if(result){
        console.log(result);
      }
      else{
        console.log("null value");
      }
    });
  }

}
