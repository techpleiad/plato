import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddNewServiceComponent } from '../add-new-service/add-new-service.component';
import { microService } from '../microService';

@Component({
  selector: 'app-add-search',
  templateUrl: './add-search.component.html',
  styleUrls: ['./add-search.component.css']
})
export class AddSearchComponent implements OnInit {
  @Output() reload_display_services = new EventEmitter();
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

}
